package dev.smartshub.shkoth.service.team;

import dev.smartshub.shkoth.service.notify.NotifyService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TeamInformationService {

    private final TeamHandlingService teamHandlingService;
    private final NotifyService notifyService;

    public TeamInformationService(TeamHandlingService teamHandlingService, NotifyService notifyService) {
        this.teamHandlingService = teamHandlingService;
        this.notifyService = notifyService;
    }

    public void sendTeamMessage(Player player, String message) {
        if (!teamHandlingService.hasTeam(player.getUniqueId())) {
            notifyService.sendChat(player, "team.not-in-a-team");
            return;
        }

        teamHandlingService.getPlayerTeam(player.getUniqueId()).getOnlineMembers().forEach(member -> {
            notifyService.sendTeamMessage(player, message);
        });
    }

    public void sendTeamInfo(Player player) {
        if (!teamHandlingService.hasTeam(player.getUniqueId())) {
            notifyService.sendChat(player, "team.not-in-a-team");
            return;
        }

        var team = teamHandlingService.getPlayerTeam(player.getUniqueId());
        notifyService.sendChat(player, "team.team-info-header");
        notifyService.sendRawMessage(player, "Leader: " + Bukkit.getOfflinePlayer(team.getLeader()).getName());
        notifyService.sendRawMessage(player, "Members:");
        team.getMembers().forEach(member -> {
            String memberName = Bukkit.getOfflinePlayer(member).getName();
            if (member.equals(team.getLeader())) {
                memberName += " (Leader)";
            }
            notifyService.sendRawMessage(player, "- " + memberName);
        });
        notifyService.sendChat(player, "team.team-info-footer");
    }

}
