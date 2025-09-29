package dev.smartshub.shkoth.listener.koth;

import dev.smartshub.shkoth.api.event.koth.PlayerEnterKothDuringRunEvent;
import dev.smartshub.shkoth.hook.discord.DiscordWebHookSender;
import dev.smartshub.shkoth.hook.placeholder.PlaceholderAPIHook;
import dev.smartshub.shkoth.service.notify.NotifyService;
import dev.smartshub.shkoth.storage.cache.PushStackCache;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PlayerEnterKothDuringRunListener implements Listener {

    private final NotifyService notifyService;
    private final DiscordWebHookSender discordWebHookSender;

    public PlayerEnterKothDuringRunListener(NotifyService notifyService,
                                            DiscordWebHookSender discordWebHookSender) {
        this.notifyService = notifyService;
        this.discordWebHookSender = discordWebHookSender;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerEnterKoth(PlayerEnterKothDuringRunEvent event) {
        PushStackCache.pushArg1(event.getKoth().getDisplayName());
        PushStackCache.pushArg2(event.getPlayer().getName());
        discordWebHookSender.send(event);
        notifyService.sendChat(event.getPlayer(), "koth.enter");
        notifyService.sendTitle(event.getPlayer(), "koth.enter.title", "koth.enter.subtitle");
        notifyService.sendActionBar(event.getPlayer(), "koth.enter");
        notifyService.playSound(event.getPlayer(), "koth.enter");
    }

}
