package dev.smartshub.shkoth.listener.koth;

import dev.smartshub.shkoth.api.event.koth.PlayerStopKothCaptureEvent;
import dev.smartshub.shkoth.hook.placeholder.PlaceholderAPIHook;
import dev.smartshub.shkoth.service.notify.NotifyService;
import dev.smartshub.shkoth.storage.cache.PushStackCache;
import org.bukkit.Bukkit;
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
        PushStackCache.pushArg1(event.getKoth().getDisplayName());
        notifyService.sendChat(event.getPlayer(), "koth.capture.stop");
        notifyService.sendBroadcastListToOnlinePlayers("koth.capture.stop");
        Bukkit.getOnlinePlayers().forEach(player -> {
            notifyService.sendTitle(player, "koth.stop-capturing.title", "koth.stop-capturing.subtitle");
            notifyService.sendActionBar(player, "koth.stop-capturing");
            notifyService.playSound(player, "koth.stop-capturing");
        });
    }

}
