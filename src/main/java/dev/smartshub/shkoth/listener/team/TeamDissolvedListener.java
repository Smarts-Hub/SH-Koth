package dev.smartshub.shkoth.listener.team;

import dev.smartshub.shkoth.api.event.team.TeamDissolvedEvent;
import dev.smartshub.shkoth.service.notify.NotifyService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class TeamDissolvedListener implements Listener {

    private final NotifyService notifyService;

    public TeamDissolvedListener(NotifyService notifyService) {
        this.notifyService = notifyService;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTeamDissolved(TeamDissolvedEvent event){
        notifyService.sendChat(event.getDissolverPlayer(), "team.disbanded-team-success");
        for(Player player : event.getTeam().getOnlineMembers()){
            notifyService.sendChat(player, "team.dissolved");
        }
    }

}
