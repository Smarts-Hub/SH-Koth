package dev.smartshub.shkoth.listener.koth;

import dev.smartshub.shkoth.api.event.koth.KothStartEvent;
import dev.smartshub.shkoth.registry.cache.SimpleKothCache;
import dev.smartshub.shkoth.service.notify.NotifyService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class KothStartListener implements Listener {

    private final NotifyService notifyService;
    private final SimpleKothCache kothCache;

    public KothStartListener(NotifyService notifyService, SimpleKothCache kothCache) {
        this.notifyService = notifyService;
        this.kothCache = kothCache;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKothStart(KothStartEvent event) {
        kothCache.setLastStarted(event.getKoth());
        notifyService.sendBroadcastListToOnlinePlayers("koth.start");
    }

}
