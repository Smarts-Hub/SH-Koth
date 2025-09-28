package dev.smartshub.shkoth.listener.team;

import dev.smartshub.shkoth.api.event.team.TeamCreatedEvent;
import dev.smartshub.shkoth.service.notify.NotifyService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class TeamCreatedListener implements Listener {

    private final NotifyService notifyService;

    public TeamCreatedListener(NotifyService notifyService) {
        this.notifyService = notifyService;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTeamCreate(TeamCreatedEvent event){
        notifyService.sendChat(event.getLeaderPlayer(), "team.team-created-success");
        notifyService.sendTitle(event.getLeaderPlayer(), "team.created.title", "team.created.subtitle");
        notifyService.playSound(event.getLeaderPlayer(), "team.created");
        notifyService.sendActionBar(event.getLeaderPlayer(), "team.created");

    }

}
