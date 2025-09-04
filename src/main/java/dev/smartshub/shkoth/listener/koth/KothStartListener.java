package dev.smartshub.shkoth.listener.koth;

import dev.smartshub.shkoth.api.event.koth.KothStartEvent;
import dev.smartshub.shkoth.hook.placeholder.PlaceholderAPIHook;
import dev.smartshub.shkoth.service.notify.NotifyService;
import dev.smartshub.shkoth.storage.cache.PushStackCache;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class KothStartListener implements Listener {

    private final NotifyService notifyService;

    public KothStartListener(NotifyService notifyService) {
        this.notifyService = notifyService;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKothStart(KothStartEvent event) {
        PushStackCache.pushArgs(event.getKoth().getDisplayName());
        notifyService.sendBroadcastListToOnlinePlayers("koth.start");
        Bukkit.getOnlinePlayers().forEach(player -> {
            notifyService.sendTitle(player, "koth.start.title", "koth.start.subtitle");
            notifyService.sendActionBar(player, "koth.start");
            notifyService.playSound(player, "koth.start");
        });
    }

}
