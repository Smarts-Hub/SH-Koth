package dev.smartshub.shkoth.service.team;

import dev.smartshub.shkoth.api.team.KothTeam;
import dev.smartshub.shkoth.api.team.track.TeamTracker;
import dev.smartshub.shkoth.service.notify.NotifyService;
import dev.smartshub.shkoth.team.HookedTeamTracker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TeamUpdatingService {

    private final NotifyService notifyService;
    private final TeamTracker tracker = HookedTeamTracker.getInstance();

    public TeamUpdatingService(NotifyService notifyService) {
        this.notifyService = notifyService;
    }

    public void createTeam(Player leader) {
        if (!tracker.canCreateTeams()) {
            notifyService.sendChat(leader, "team.external-plugin-active");
            return;
        }

        try {
            KothTeam team = tracker.createTeam(leader.getUniqueId());
            notifyService.sendChat(leader, "team.created");
        } catch (IllegalArgumentException e) {
            notifyService.sendChat(leader, "team.already-in-team");
        }
    }

    public void disbandTeam(Player leader) {
        if (!tracker.canManageTeams()) {
            notifyService.sendChat(leader, "team.cannot-manage-external");
            return;
        }

        KothTeam team = tracker.getTeamFrom(leader.getUniqueId());
        if (team == null || !team.isLeader(leader.getUniqueId())) {
            notifyService.sendChat(leader, "team.not-leader");
            return;
        }

        team.getOnlineMembers().forEach(player -> notifyService.sendChat(player, "team.disbanded"));

        tracker.disbandTeam(leader.getUniqueId());
    }

    public void leaveTeam(Player player) {
        if (!tracker.canManageTeams()) {
            notifyService.sendChat(player, "team.cannot-manage-external");
            return;
        }

        KothTeam team = tracker.getTeamFrom(player.getUniqueId());
        if (team == null) {
            notifyService.sendChat(player, "team.no-team");
            return;
        }

        if (team.isLeader(player.getUniqueId())) {
            notifyService.sendChat(player, "team.leader-cannot-leave");
            return;
        }

        tracker.removeMemberFromTeam(player.getUniqueId());
        notifyService.sendChat(player, "team.left");
    }

    public void kickMember(Player kicker, String target) {
        if (!tracker.canManageTeams()) {
            notifyService.sendChat(kicker, "team.cannot-manage-external");
            return;
        }

        KothTeam team = tracker.getTeamFrom(kicker.getUniqueId());
        if (team == null || !team.isLeader(kicker.getUniqueId())) {
            notifyService.sendChat(kicker, "team.not-leader");
            return;
        }

        Player targetPlayer = Bukkit.getPlayerExact(target);
        if (targetPlayer == null || !team.contains(targetPlayer.getUniqueId())) {
            notifyService.sendChat(kicker, "team.not-a-member");
            return;
        }

        if (team.isLeader(targetPlayer.getUniqueId())) {
            notifyService.sendChat(kicker, "team.cannot-kick-leader");
            return;
        }

        tracker.removeMemberFromTeam(targetPlayer.getUniqueId());
        notifyService.sendChat(kicker, "team.kicked");

        if (targetPlayer.isOnline()) {
            notifyService.sendChat(targetPlayer, "team.kicked-from-team");
        }
    }

    public void setLeader(Player currentLeader, String newLeaderName) {
        if (!tracker.canManageTeams()) {
            notifyService.sendChat(currentLeader, "team.cannot-manage-external");
            return;
        }

        KothTeam team = tracker.getTeamFrom(currentLeader.getUniqueId());
        if (team == null || !team.isLeader(currentLeader.getUniqueId())) {
            notifyService.sendChat(currentLeader, "team.not-leader");
            return;
        }

        Player newLeader = Bukkit.getPlayerExact(newLeaderName);
        if (newLeader == null || !team.contains(newLeader.getUniqueId())) {
            notifyService.sendChat(currentLeader, "team.not-a-member");
            return;
        }

        tracker.transferLeadership(currentLeader.getUniqueId(), newLeader.getUniqueId());

        KothTeam updatedTeam = tracker.getTeamFrom(newLeader.getUniqueId());
        if (updatedTeam != null) {
            updatedTeam.getOnlineMembers()
                    .forEach(player -> notifyService.sendChat(player, "team.new-leader"));
        }
    }

}
