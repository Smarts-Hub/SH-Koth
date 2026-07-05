package dev.smartshub.shkoth.listener.player;

import dev.smartshub.shkoth.service.reward.OfflineRewardStorage;
import dev.smartshub.shkoth.storage.cache.PlayerStatsCache;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerJoinListener implements Listener {

    private final PlayerStatsCache playerStatsCache;

    public PlayerJoinListener(PlayerStatsCache playerStatsCache) {
        this.playerStatsCache = playerStatsCache;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        playerStatsCache.preload(event.getPlayer());

        OfflineRewardStorage storage = OfflineRewardStorage.get();
        if (storage != null && storage.hasPending(event.getPlayer().getUniqueId())) {
            // Delay 20 ticks until the player is fully in the world, so the inventory is ready.
            Bukkit.getScheduler().runTaskLater(
                    JavaPlugin.getProvidingPlugin(PlayerJoinListener.class),
                    () -> storage.giveTo(event.getPlayer()),
                    20L);
        }
    }

}
