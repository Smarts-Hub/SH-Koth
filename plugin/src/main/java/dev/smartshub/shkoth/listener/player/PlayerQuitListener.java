package dev.smartshub.shkoth.listener.player;

import dev.smartshub.shkoth.storage.cache.PlayerStatsCache;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final PlayerStatsCache playerStatsCache;

    public PlayerQuitListener(PlayerStatsCache playerStatsCache) {
        this.playerStatsCache = playerStatsCache;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        playerStatsCache.invalidate(event.getPlayer().getUniqueId());
    }

}
