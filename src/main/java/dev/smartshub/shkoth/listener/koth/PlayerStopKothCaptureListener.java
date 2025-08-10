package dev.smartshub.shkoth.listener.koth;

import dev.smartshub.shkoth.api.event.koth.PlayerStopKothCaptureEvent;
import dev.smartshub.shkoth.registry.cache.SimpleKothCache;
import dev.smartshub.shkoth.service.notify.NotifyService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PlayerStopKothCaptureListener implements Listener {

    private final NotifyService notifyService;
    private final SimpleKothCache kothCache;

    public PlayerStopKothCaptureListener(NotifyService notifyService, SimpleKothCache kothCache) {
        this.notifyService = notifyService;
        this.kothCache = kothCache;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerStopKothCaptureEvent(PlayerStopKothCaptureEvent event) {
        kothCache.setLastStopCaptured(event.getKoth());
        notifyService.sendChat(event.getPlayer(), "koth.capture.stop");
        notifyService.sendBroadcastListToOnlinePlayers("koth.capture.stop.broadcast");
    }

}
