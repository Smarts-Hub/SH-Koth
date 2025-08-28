package dev.smartshub.shkoth.listener.player;

import dev.smartshub.shkoth.storage.cache.PlayerStatsCache;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final PlayerStatsCache playerStatsCache;

    public PlayerJoinListener(PlayerStatsCache playerStatsCache) {
        this.playerStatsCache = playerStatsCache;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        playerStatsCache.preload(event.getPlayer());
    }

}
