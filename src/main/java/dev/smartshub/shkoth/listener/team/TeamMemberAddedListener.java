package dev.smartshub.shkoth.listener.team;

import dev.smartshub.shkoth.api.event.team.TeamMemberAddedEvent;
import dev.smartshub.shkoth.service.notify.NotifyService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class TeamMemberAddedListener implements Listener {

    private final NotifyService notifyService;

    public TeamMemberAddedListener(NotifyService notifyService) {
        this.notifyService = notifyService;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTeamMemberAdded(TeamMemberAddedEvent event){
        notifyService.sendChat(event.getAddedPlayer(), "team.added-to-team");
        for(Player player : event.getOldTeam().getOnlineMembers()){
            notifyService.sendChat(player, "team.member-added");
        }
    }

}
