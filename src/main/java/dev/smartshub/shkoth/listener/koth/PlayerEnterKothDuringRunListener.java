package dev.smartshub.shkoth.listener.koth;

import dev.smartshub.shkoth.api.event.koth.PlayerEnterKothDuringRunEvent;
import dev.smartshub.shkoth.service.notify.NotifyService;
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
        notifyService.sendChat(event.getPlayer(), "koth.enter");
    }

}
