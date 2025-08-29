package dev.smartshub.shkoth.service.koth;

import dev.smartshub.shkoth.api.koth.Koth;
import dev.smartshub.shkoth.registry.KothRegistry;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public class RefreshInsideKothService {

    private final KothRegistry kothRegistry;

    public RefreshInsideKothService(KothRegistry kothRegistry) {
        this.kothRegistry = kothRegistry;
    }

    public void refreshInsideKoth() {
        Collection<Koth> runningKoths = kothRegistry.getRunning();

        if (runningKoths.isEmpty()) {
            return;
        }

        runningKoths.forEach(this::refreshKothPlayers);
    }

    private void refreshKothPlayers(Koth koth) {
        World world = getKothWorld(koth);
        if (world == null) {
            return;
        }

        Collection<? extends Player> worldPlayers = world.getPlayers();
        if (worldPlayers.isEmpty()) {
            handleAllPlayersLeft(koth);
            return;
        }

        Set<UUID> currentPlayersInside = koth.getPlayersInside();

        for (Player player : worldPlayers) {
            if (!koth.canPlayerCapture(player)) {
                if (currentPlayersInside.contains(player.getUniqueId())) {
                    System.out.println("DEBUG - Removing player who can no longer capture: " + player.getName());
                    koth.playerLeave(player);
                }
                continue;
            }

            processPlayer(koth, player, currentPlayersInside);
        }

        handleOfflineOrWorldChangedPlayers(koth, worldPlayers);
    }

    private void processPlayer(Koth koth, Player player, Set<UUID> currentPlayersInside) {
        UUID playerId = player.getUniqueId();

        if (!koth.canPlayerCapture(player)) {
            System.out.println("DEBUG - Ignoring player who cannot capture: " + player.getName() +
                    " (GameMode: " + player.getGameMode() + ", Dead: " + player.isDead() + ")");

            boolean wasInsideArea = currentPlayersInside.contains(playerId);
            if (wasInsideArea) {
                System.out.println("DEBUG - Removing player who can no longer capture: " + player.getName());
                koth.playerLeave(player);
            }
            return;
        }

        boolean isInsideArea = koth.isInsideArea(player);
        boolean wasInsideArea = koth.getPlayersInside().contains(playerId);

        System.out.println("DEBUG - processPlayer: " + player.getName() +
                ", isInsideArea: " + isInsideArea +
                ", wasInsideArea: " + wasInsideArea +
                ", kothInsideSetSize: " + koth.getPlayersInside().size() +
                ", currentPlayersInsideSize: " + currentPlayersInside.size());

        if (isInsideArea && !wasInsideArea) {
            System.out.println("DEBUG - Calling playerEnter for: " + player.getName());
            koth.playerEnter(player);
            System.out.println("DEBUG - After playerEnter, koth inside set size: " + koth.getPlayersInside().size());
        } else if (!isInsideArea && wasInsideArea) {
            System.out.println("DEBUG - Calling playerLeave for: " + player.getName());
            koth.playerLeave(player);
        } else if (isInsideArea && wasInsideArea) {
            System.out.println("DEBUG - Player already inside, validating eligibility");
            validatePlayerStillEligible(koth, player);
        }
    }

    private void handleOfflineOrWorldChangedPlayers(Koth koth, Collection<? extends Player> worldPlayers) {
        Set<UUID> currentPlayersInside = koth.getPlayersInside();
        if (currentPlayersInside.isEmpty()) {
            return;
        }

        Set<UUID> worldPlayerIds = worldPlayers.stream()
                .map(Player::getUniqueId)
                .collect(java.util.stream.Collectors.toSet());

        currentPlayersInside.stream()
                .filter(playerId -> !worldPlayerIds.contains(playerId))
                .forEach(playerId -> {
                    Player player = Bukkit.getPlayer(playerId);
                    if (player != null) {
                        koth.playerLeave(player);
                    } else {
                        koth.removePlayerDirectly(playerId);
                    }
                });
    }

    private void validatePlayerStillEligible(Koth koth, Player player) {
        dev.smartshub.shkoth.koth.Koth konkretKoth = (dev.smartshub.shkoth.koth.Koth) koth;

        if (!konkretKoth.isPlayerEligibleToStay(player)) {
            koth.playerLeave(player);
        }
    }

    private void handleAllPlayersLeft(Koth koth) {
        Set<UUID> currentPlayersInside = koth.getPlayersInside();
        if (!currentPlayersInside.isEmpty()) {
            currentPlayersInside.forEach(playerId -> {
                Player player = Bukkit.getPlayer(playerId);
                if (player != null) {
                    koth.playerLeave(player);
                } else {
                    koth.removePlayerDirectly(playerId);
                }
            });
        }
    }

    private World getKothWorld(Koth koth) {
        try {
            return Bukkit.getWorld(koth.getArea().worldName());
        } catch (Exception e) {
            Bukkit.getLogger().severe("Error retrieving world for Koth " + koth.getId() + ": " + e.getMessage());
            return null;
        }
    }
}
