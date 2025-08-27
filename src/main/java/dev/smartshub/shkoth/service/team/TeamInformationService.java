package dev.smartshub.shkoth.service.team;

import dev.smartshub.shkoth.api.team.KothTeam;
import dev.smartshub.shkoth.api.team.track.TeamTracker;
import dev.smartshub.shkoth.service.notify.NotifyService;
import dev.smartshub.shkoth.team.HookedTeamTracker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamInformationService {

    private final NotifyService notifyService;
    private final TeamTracker tracker = HookedTeamTracker.getInstance();

    public TeamInformationService(NotifyService notifyService) {
        this.notifyService = notifyService;
    }

    public void sendTeamMessage(Player sender, String message) {
        KothTeam team = tracker.getTeamFrom(sender.getUniqueId());
        if (team == null) {
            notifyService.sendChat(sender, "team.no-team");
            return;
        }

        String formatted = "§a[Team] " + sender.getName() + ": §f" + message;
        for (UUID memberId : team.getMembers()) {
            Player member = Bukkit.getPlayer(memberId);
            if (member != null && member.isOnline()) {
                member.sendMessage(formatted);
            }
        }
    }

    public void sendTeamInfo(Player player) {
        KothTeam team = tracker.getTeamFrom(player.getUniqueId());
        if (team == null) {
            notifyService.sendChat(player, "team.no-team");
            return;
        }

        notifyService.sendChat(player, "team.info.header");
        notifyService.sendChat(player, "team.info.provider");

        team.getOnlineMembers().forEach(p -> {
            if (team.isLeader(p.getUniqueId())) {
                notifyService.sendChat(player, "team.info.leader");
            } else {
                notifyService.sendChat(player, "team.info.member");
            }
        });

        if (!tracker.canManageTeams()) {
            notifyService.sendChat(player, "team.info.external-managed");
        }
    }
}
