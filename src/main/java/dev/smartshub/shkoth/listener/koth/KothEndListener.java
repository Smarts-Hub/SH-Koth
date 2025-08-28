package dev.smartshub.shkoth.listener.koth;

import dev.smartshub.shkoth.api.event.koth.KothEndEvent;
import dev.smartshub.shkoth.service.notify.NotifyService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class KothEndListener implements Listener {

    private final NotifyService notifyService;

    public KothEndListener(NotifyService notifyService) {
        this.notifyService = notifyService;}

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKothEnd(KothEndEvent event) {
        notifyService.sendBroadcastListToOnlinePlayers("koth.end");
    }

}
