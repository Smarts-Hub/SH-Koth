package dev.smartshub.shkoth.service.team;

import dev.smartshub.shkoth.api.team.KothTeam;
import dev.smartshub.shkoth.api.team.track.TeamTracker;
import dev.smartshub.shkoth.service.notify.NotifyService;
import dev.smartshub.shkoth.team.HookedTeamTracker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TeamInvitationService {

    private final NotifyService notifyService;
    private final TeamTracker tracker = HookedTeamTracker.getInstance();
    private final Map<UUID, UUID> pendingInvites = new HashMap<>(); // target -> leader

    public TeamInvitationService(NotifyService notifyService) {
        this.notifyService = notifyService;
    }

    public void invite(Player leader, String target) {
        KothTeam team = tracker.getTeamFrom(leader.getUniqueId());
        if (team == null || !team.isLeader(leader.getUniqueId())) {
            notifyService.sendChat(leader, "team.not-leader");
            return;
        }

        Player targetPlayer = Bukkit.getPlayerExact(target);
        if (targetPlayer == null) {
            notifyService.sendChat(leader, "player-not-found");
            return;
        }

        if (tracker.isTeamMember(targetPlayer.getUniqueId())) {
            notifyService.sendChat(leader, "team.player-already-in-team");
            return;
        }

        pendingInvites.put(targetPlayer.getUniqueId(), leader.getUniqueId());

        team.getOnlineMembers()
                .forEach(player -> notifyService.sendChat(player, "team.invite-sent"));

        notifyService.sendChat(targetPlayer, "team.invited");
    }

    public void acceptInvite(Player player) {
        UUID leaderId = pendingInvites.remove(player.getUniqueId());
        if (leaderId == null) {
            notifyService.sendChat(player, "team.no-pending-invite");
            return;
        }

        if (!tracker.canManageTeams()) {
            notifyService.sendChat(player, "team.cannot-join-external");
            return;
        }

        Optional<KothTeam> teamOpt = tracker.getTeamByLeader(leaderId);
        if (teamOpt.isEmpty()) {
            notifyService.sendChat(player, "team.invite-expired");
            return;
        }

        if (tracker.isTeamMember(player.getUniqueId())) {
            notifyService.sendChat(player, "team.already-in-team");
            return;
        }

        boolean success = tracker.addMemberToTeam(player.getUniqueId(), leaderId);
        if (success) {
            KothTeam updatedTeam = tracker.getTeamFrom(player.getUniqueId());
            if (updatedTeam != null) {
                updatedTeam.getOnlineMembers()
                        .forEach(member -> notifyService.sendChat(member, "team.new-member"));
            }
        } else {
            notifyService.sendChat(player, "team.join-failed");
        }
    }

    public void declineInvite(Player player) {
        UUID leaderId = pendingInvites.remove(player.getUniqueId());
        if (leaderId == null) {
            notifyService.sendChat(player, "team.no-pending-invite");
            return;
        }

        Optional<KothTeam> teamOpt = tracker.getTeamByLeader(leaderId);
        teamOpt.ifPresent(kothTeam -> kothTeam.getOnlineMembers()
                .forEach(p -> notifyService.sendChat(p, "team.invite-declined")));

        notifyService.sendChat(player, "team.invite-declined-self");
    }
}
