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
            processPlayer(koth, player, currentPlayersInside);
        }

        handleOfflineOrWorldChangedPlayers(koth, worldPlayers);
    }

    private void processPlayer(Koth koth, Player player, Set<UUID> currentPlayersInside) {
        UUID playerId = player.getUniqueId();
        boolean isInsideArea = koth.isInsideArea(player);
        boolean wasInsideArea = currentPlayersInside.contains(playerId);

        if (isInsideArea && !wasInsideArea) {
            koth.playerEnter(player);
        } else if (!isInsideArea && wasInsideArea) {
            koth.playerLeave(player);
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
