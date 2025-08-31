package dev.smartshub.shkoth.listener.koth;

import dev.smartshub.shkoth.api.event.koth.PlayerStartKothCaptureEvent;
import dev.smartshub.shkoth.hook.placeholder.PlaceholderAPIHook;
import dev.smartshub.shkoth.service.notify.NotifyService;
import org.bukkit.Bukkit;
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
        PlaceholderAPIHook.pushArgs(event.getKoth().getDisplayName());
        notifyService.sendChat(event.getPlayer(), "koth.capture.start");
        notifyService.sendBroadcastListToOnlinePlayers("koth.capture.start");
        Bukkit.getOnlinePlayers().forEach(player -> {
            notifyService.sendTitle(player, "koth.start-capturing.title", "koth.start-capturing.subtitle");
            notifyService.sendActionBar(player, "koth.start-capturing");
            notifyService.playSound(player, "koth.start-capturing");
        });
    }

}