package dev.smartshub.shkoth.listener.koth;

import dev.smartshub.shkoth.api.event.koth.PlayerStartKothCaptureEvent;
import dev.smartshub.shkoth.hook.discord.DiscordWebHookSender;
import dev.smartshub.shkoth.hook.placeholder.PlaceholderAPIHook;
import dev.smartshub.shkoth.service.notify.NotifyService;
import dev.smartshub.shkoth.storage.cache.PushStackCache;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PlayerStartKothCaptureListener implements Listener {

    private final NotifyService notifyService;
    private final DiscordWebHookSender discordWebHookSender;

    public PlayerStartKothCaptureListener(NotifyService notifyService,
                                          DiscordWebHookSender discordWebHookSender) {
        this.notifyService = notifyService;
        this.discordWebHookSender = discordWebHookSender;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerStartKothCapture(PlayerStartKothCaptureEvent event) {
        PushStackCache.pushArg1(event.getKoth().getDisplayName());
        PushStackCache.pushArg2(event.getPlayer().getName());
        discordWebHookSender.send(event);
        notifyService.sendChat(event.getPlayer(), "koth.capture.start");
        Bukkit.getOnlinePlayers().forEach(player -> {
            notifyService.sendTitle(player, "koth.start-capturing.title", "koth.start-capturing.subtitle");
            notifyService.sendActionBar(player, "koth.start-capturing");
            notifyService.playSound(player, "koth.start-capturing");
        });
    }

}