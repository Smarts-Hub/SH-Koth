package dev.smartshub.shkoth.listener.team;

import dev.smartshub.shkoth.api.event.team.MemberKickedFromTeamEvent;
import dev.smartshub.shkoth.service.notify.NotifyService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class MemberKickedFromTeamListener implements Listener {

    private final NotifyService notifyService;

    public MemberKickedFromTeamListener(NotifyService notifyService) {
        this.notifyService = notifyService;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMemberKickedFromTeam(MemberKickedFromTeamEvent event) {
        notifyService.sendChat(event.getKickedPlayer(), "team.kicked-from-team");
        for (var player : event.getOldTeam().getOnlineMembers()) {
            notifyService.sendChat(player, "team.member-kicked");
        }
        notifyService.sendChat(event.getKickerPlayer(), "team.kick-member-success");
    }
}
