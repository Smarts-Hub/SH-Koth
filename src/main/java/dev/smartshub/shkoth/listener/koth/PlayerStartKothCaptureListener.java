package dev.smartshub.shkoth.listener.koth;

import dev.smartshub.shkoth.api.event.koth.PlayerStartKothCaptureEvent;
import dev.smartshub.shkoth.service.notify.NotifyService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PlayerStartKothCaptureListener implements Listener {

    private final NotifyService notifyService;

    public PlayerStartKothCaptureListener(NotifyService notifyService) {
        this.notifyService = notifyService;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerStartKothCapture(PlayerStartKothCaptureEvent event) {
        notifyService.sendChat(event.getPlayer(), "koth.capture.start");
        notifyService.sendBroadcastListToOnlinePlayers("koth.capture.start.broadcast");
    }

}