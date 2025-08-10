package dev.smartshub.shkoth.listener.team;

import dev.smartshub.shkoth.api.event.team.TeamMemberRemovedEvent;
import dev.smartshub.shkoth.service.notify.NotifyService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class TeamMemberRemovedListener implements Listener {

    private final NotifyService notifyService;

    public TeamMemberRemovedListener(NotifyService notifyService) {
        this.notifyService = notifyService;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTeamMemberRemoved(TeamMemberRemovedEvent event){
        notifyService.sendChat(event.getRemovedPlayer(), "team.removed-from-team");
        for (var player : event.getNewTeam().getOnlineMembers()) {
            notifyService.sendChat(player, "team.member-removed");
        }

    }

}
