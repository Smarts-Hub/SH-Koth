package dev.smartshub.shkoth.listener.koth;

import dev.smartshub.shkoth.api.event.koth.KothEndEvent;
import dev.smartshub.shkoth.registry.cache.SimpleKothCache;
import dev.smartshub.shkoth.service.notify.NotifyService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class KothEndListener implements Listener {

    private final NotifyService notifyService;
    private final SimpleKothCache kothCache;

    public KothEndListener(NotifyService notifyService, SimpleKothCache kothCache) {
        this.notifyService = notifyService;
        this.kothCache = kothCache;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKothEnd(KothEndEvent event) {
        kothCache.setLastEnded(event.getKoth());
        notifyService.sendBroadcastListToOnlinePlayers("koth.end");
    }

}
