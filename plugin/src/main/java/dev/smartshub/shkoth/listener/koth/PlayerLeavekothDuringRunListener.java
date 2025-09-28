package dev.smartshub.shkoth.listener.koth;

import dev.smartshub.shkoth.api.event.koth.PlayerLeaveKothDuringRunEvent;
import dev.smartshub.shkoth.service.notify.NotifyService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PlayerLeavekothDuringRunListener implements Listener {

    private final NotifyService notifyService;

    public PlayerLeavekothDuringRunListener(NotifyService notifyService) {
        this.notifyService = notifyService;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLeaveKoth(PlayerLeaveKothDuringRunEvent event) {
        notifyService.sendChat(event.getPlayer(), "koth.leave");
        notifyService.sendTitle(event.getPlayer(), "koth.leave.title", "koth.leave.subtitle");
        notifyService.sendActionBar(event.getPlayer(), "koth.leave");
        notifyService.playSound(event.getPlayer(), "koth.leave");
    }

}
