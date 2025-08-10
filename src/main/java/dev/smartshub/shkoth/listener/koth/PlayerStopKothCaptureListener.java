package dev.smartshub.shkoth.listener.koth;

import dev.smartshub.shkoth.api.event.koth.PlayerStopKothCaptureEvent;
import dev.smartshub.shkoth.service.notify.NotifyService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PlayerStopKothCaptureListener implements Listener {

    private final NotifyService notifyService;

    public PlayerStopKothCaptureListener(NotifyService notifyService) {
        this.notifyService = notifyService;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerStopKothCaptureEvent(PlayerStopKothCaptureEvent event) {
        notifyService.sendChat(event.getPlayer(), "koth.capture.stop");
        notifyService.sendBroadcastListToOnlinePlayers("koth.capture.stop.broadcast");
    }

}
