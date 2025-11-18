package dev.smartshub.shkoth.service.koth;

import dev.smartshub.shkoth.api.koth.Koth;
import dev.smartshub.shkoth.registry.KothRegistry;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class RefreshInsideKothService {

    private final KothRegistry kothRegistry;

    public RefreshInsideKothService(KothRegistry kothRegistry) {
        this.kothRegistry = kothRegistry;
    }

    public void refreshInsideKoth() {
        kothRegistry.getRunning().forEach(this::refreshKothPlayers);
    }

    private void refreshKothPlayers(Koth koth) {
        World world = getKothWorld(koth);
        if (world == null)
            return;

        Collection<? extends Player> worldPlayers = world.getPlayers();
        Set<UUID> currentPlayersInside = koth.getPlayersInside();

        if (worldPlayers.isEmpty()) {
            removeAllPlayers(koth, currentPlayersInside);
            return;
        }

        worldPlayers.forEach(player -> {
            processPlayer(koth, player, currentPlayersInside);
        });
        removeOfflinePlayers(koth, worldPlayers, currentPlayersInside);
    }

    private void processPlayer(Koth koth, Player player, Set<UUID> currentPlayersInside) {
        UUID playerId = player.getUniqueId();
        boolean isInsideArea = koth.isInsideArea(player);
        boolean wasInsideArea = currentPlayersInside.contains(playerId);
        boolean canCapture = koth.canPlayerCapture(player);

        if (!canCapture && wasInsideArea) {
            koth.playerLeave(player);
            return;
        }

        if (!canCapture)
            return;

        if (isInsideArea && !wasInsideArea) {
            koth.playerEnter(player);
        } else if (!isInsideArea && wasInsideArea) {
            koth.playerLeave(player);
        } else if (isInsideArea && wasInsideArea) {
            validatePlayerEligibility(koth, player);
        }
    }

    private void validatePlayerEligibility(Koth koth, Player player) {
        dev.smartshub.shkoth.koth.Koth concreteKoth = (dev.smartshub.shkoth.koth.Koth) koth;
        if (!concreteKoth.isPlayerEligibleToStay(player)) {
            koth.playerLeave(player);
        }
    }

    private void removeOfflinePlayers(Koth koth, Collection<? extends Player> worldPlayers,
            Set<UUID> currentPlayersInside) {
        Set<UUID> worldPlayerIds = worldPlayers.stream()
                .map(Player::getUniqueId)
                .collect(Collectors.toSet());

        Iterator<UUID> iterator = currentPlayersInside.iterator();
        while (iterator.hasNext()) {
            UUID playerId = iterator.next();
            if (!worldPlayerIds.contains(playerId)) {
                Player player = Bukkit.getPlayer(playerId);
                if (player != null) {
                    koth.playerLeave(player);
                } else {
                    koth.removePlayerDirectly(playerId);
                }
                iterator.remove();
            }
        }
    }

    private void removeAllPlayers(Koth koth, Set<UUID> currentPlayersInside) {
        currentPlayersInside.forEach(playerId -> {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                koth.playerLeave(player);
            } else {
                koth.removePlayerDirectly(playerId);
            }
        });
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