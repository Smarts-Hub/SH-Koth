package dev.smartshub.shkoth.listener.koth;

import dev.smartshub.shkoth.api.event.koth.PlayerStartKothCaptureEvent;
import dev.smartshub.shkoth.registry.cache.SimpleKothCache;
import dev.smartshub.shkoth.service.notify.NotifyService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PlayerStartKothCaptureListener implements Listener {

    private final NotifyService notifyService;
    private final SimpleKothCache kothCache;

    public PlayerStartKothCaptureListener(NotifyService notifyService, SimpleKothCache kothCache) {
        this.notifyService = notifyService;
        this.kothCache = kothCache;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerStartKothCapture(PlayerStartKothCaptureEvent event) {
        kothCache.setLastStartCaptured(event.getKoth());
        notifyService.sendChat(event.getPlayer(), "koth.capture.start");
        notifyService.sendBroadcastListToOnlinePlayers("koth.capture.start.broadcast");
    }

}