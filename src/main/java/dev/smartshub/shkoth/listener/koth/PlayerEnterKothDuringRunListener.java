package dev.smartshub.shkoth.listener.koth;

import dev.smartshub.shkoth.api.event.koth.PlayerEnterKothDuringRunEvent;
import dev.smartshub.shkoth.hook.placeholder.PlaceholderAPIHook;
import dev.smartshub.shkoth.service.notify.NotifyService;
import dev.smartshub.shkoth.storage.cache.PushStackCache;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PlayerEnterKothDuringRunListener implements Listener {

    private final NotifyService notifyService;

    public PlayerEnterKothDuringRunListener(NotifyService notifyService) {
        this.notifyService = notifyService;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerEnterKoth(PlayerEnterKothDuringRunEvent event) {
        PushStackCache.pushArgs(event.getKoth().getDisplayName());
        notifyService.sendChat(event.getPlayer(), "koth.enter");
        notifyService.sendTitle(event.getPlayer(), "koth.enter.title", "koth.enter.subtitle");
        notifyService.sendActionBar(event.getPlayer(), "koth.enter");
        notifyService.playSound(event.getPlayer(), "koth.enter");
    }

}
