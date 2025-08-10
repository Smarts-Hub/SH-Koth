package dev.smartshub.shkoth.listener.koth;

import dev.smartshub.shkoth.api.event.koth.KothStartEvent;
import dev.smartshub.shkoth.service.notify.NotifyService;
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
        notifyService.sendBroadcastListToOnlinePlayers("koth.start");
    }

}
