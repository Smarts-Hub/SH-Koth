package dev.smartshub.shkoth.service.koth;

import dev.smartshub.shkoth.api.koth.Koth;
import dev.smartshub.shkoth.registry.KothRegistry;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class RefreshInsideKothService {

    private final KothRegistry kothRegistry;

    public RefreshInsideKothService(KothRegistry kothRegistry) {
        this.kothRegistry = kothRegistry;
    }

    /**
     * Refreshes all running KOTHs using an optimized, player-centric approach.
     * KOTHs are grouped by world to minimize redundant API calls and iterations.
     */
    public void refreshInsideKoth() {
        // Step 1: Group running KOTHs by their world. This is the core optimization,
        // preventing us from repeatedly getting the same world and its player list.
        Map<World, List<Koth>> kothsByWorld = new HashMap<>();
        for (Koth koth : kothRegistry.getRunning()) {
            World world = getKothWorld(koth);
            if (world != null) {
                kothsByWorld.computeIfAbsent(world, w -> new ArrayList<>()).add(koth);
            }
        }

        // Step 2: Process each world that contains at least one running KOTH.
        kothsByWorld.forEach(this::refreshKothsInWorld);
    }

    /**
     * Processes all players and KOTHs within a single world.
     *
     * @param world The world to process.
     * @param kothsInWorld The list of running KOTHs in that world.
     */
    private void refreshKothsInWorld(World world, List<Koth> kothsInWorld) {
        // Step 3: Get the list of players in this world only ONCE.
        Collection<? extends Player> playersInWorld = world.getPlayers();

        // Handle the edge case where a world has KOTHs but no players.
        if (playersInWorld.isEmpty()) {
            kothsInWorld.forEach(this::removeAllPlayers);
            return;
        }

        // --- Player-centric processing loop ---
        // Step 4: For each player, check their status against all KOTHs in their world.
        for (Player player : playersInWorld) {
            for (Koth koth : kothsInWorld) {
                processPlayer(koth, player);
            }
        }

        // --- Cleanup phase for offline or world-changed players ---
        // Step 5: After processing all online players, efficiently remove anyone who is no longer valid
        // from each KOTH's internal set.
        Set<UUID> playersInWorldIds = playersInWorld.stream()
                .map(Player::getUniqueId)
                .collect(Collectors.toSet());

        for (Koth koth : kothsInWorld) {
            removeInvalidPlayers(koth, playersInWorldIds);
        }
    }

    /**
     * Processes a single player's interaction with a single KOTH.
     * This contains the original core game logic.
     *
     * @param koth The KOTH to check against.
     * @param player The player to process.
     */
    private void processPlayer(Koth koth, Player player) {
        UUID playerId = player.getUniqueId();
        Set<UUID> currentPlayersInside = koth.getPlayersInside();

        boolean isInsideArea = koth.isInsideArea(player);
        boolean wasInsideArea = currentPlayersInside.contains(playerId);
        boolean canCapture = koth.canPlayerCapture(player);

        if (!canCapture) {
            // If the player was inside but can no longer capture, make them leave.
            if (wasInsideArea) {
                koth.playerLeave(player);
            }
            return;
        }

        // Player can capture, now check their position.
        if (isInsideArea && !wasInsideArea) {
            koth.playerEnter(player);
        } else if (!isInsideArea && wasInsideArea) {
            koth.playerLeave(player);
        } else if (isInsideArea && wasInsideArea) {
            // Player is still inside, validate they are still eligible to stay.
            validatePlayerEligibility(koth, player);
        }
    }

    /**
     * Checks if a player who is already inside the KOTH area is still eligible to be there.
     * This method is now clean and relies on the Koth interface.
     *
     * @param koth The KOTH.
     * @param player The player.
     */
    private void validatePlayerEligibility(Koth koth, Player player) {
        // FINAL CHANGE: The unsafe cast is gone. We now safely call the method on the interface.
        if (!koth.isPlayerEligibleToStay(player)) {
            koth.playerLeave(player);
        }
    }

    /**
     * Removes players from a KOTH's 'inside' set if they are no longer in the world (e.g., logged off).
     * This uses the efficient `removeIf` method, which safely modifies the set during iteration.
     *
     * @param koth The KOTH to clean up.
     * @param playersInWorldIds A pre-computed set of UUIDs for all players currently in the KOTH's world.
     */
    private void removeInvalidPlayers(Koth koth, Set<UUID> playersInWorldIds) {
        koth.getPlayersInside().removeIf(playerId -> {
            // Remove if the player is NOT in the set of players currently in this world.
            if (!playersInWorldIds.contains(playerId)) {
                Player player = Bukkit.getPlayer(playerId);
                if (player != null) {
                    // This handles players who changed worlds during the tick.
                    koth.playerLeave(player);
                } else {
                    // This handles the common case: player logged off.
                    koth.removePlayerDirectly(playerId);
                }
                return true; // Returning true removes the UUID from the 'playersInside' set.
            }
            return false; // Returning false keeps the UUID.
        });
    }

    /**
     * Efficiently removes all players from a KOTH, used when its world becomes empty.
     *
     * @param koth The KOTH to clear.
     */
    private void removeAllPlayers(Koth koth) {
        // Since we know the world is empty, Bukkit.getPlayer will be null for every UUID.
        // We can directly call the most lightweight removal method.
        // We iterate over a copy of the set to prevent ConcurrentModificationException if
        // koth.removePlayerDirectly modifies the same set we are iterating over.
        new HashSet<>(koth.getPlayersInside()).forEach(koth::removePlayerDirectly);
    }

    /**
     * Safely retrieves the World for a given KOTH, logging any errors.
     *
     * @param koth The KOTH.
     * @return The World object, or null if not found or an error occurs.
     */
    private World getKothWorld(Koth koth) {
        try {
            return Bukkit.getWorld(koth.getArea().worldName());
        } catch (Exception e) {
            Bukkit.getLogger().severe("Error retrieving world for Koth " + koth.getId() + ": " + e.getMessage());
            return null;
        }
    }
}