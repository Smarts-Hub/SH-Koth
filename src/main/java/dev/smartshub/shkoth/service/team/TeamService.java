package dev.smartshub.shkoth.service.team;

import dev.smartshub.shkoth.api.team.KothTeam;
import dev.smartshub.shkoth.api.team.track.TeamTracker;
import dev.smartshub.shkoth.service.notify.NotifyService;
import dev.smartshub.shkoth.team.track.HookedTeamTracker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class TeamService {

    private final NotifyService notifyService;
    private final TeamTracker tracker = HookedTeamTracker.getInstance();
    private final Map<UUID, UUID> pendingInvites = new HashMap<>(); // target -> leader

    public TeamService(NotifyService notifyService) {
        this.notifyService = notifyService;
    }

    public KothTeam createTeam(Player leader) {
        if (!tracker.canCreateTeams()) {
            notifyService.sendChat(leader, "team.external-plugin-active");
            return null;
        }

        try {
            KothTeam team = tracker.createTeam(leader.getUniqueId());
            notifyService.sendChat(leader, "team.created");
            return team;
        } catch (IllegalArgumentException e) {
            notifyService.sendChat(leader, "team.already-in-team");
            return null;
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
        if (teamOpt.isPresent()) {
            teamOpt.get().getOnlineMembers()
                    .forEach(p -> notifyService.sendChat(p, "team.invite-declined"));
        }

        notifyService.sendChat(player, "team.invite-declined-self");
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

    public KothTeam getTeam(Player player) {
        return tracker.getTeamFrom(player.getUniqueId());
    }

    public Collection<KothTeam> listTeams() {
        return tracker.getAllTeams();
    }

    public String getActiveTeamSystem() {
        return tracker.getActiveProvider();
    }

    public boolean canCreateTeams() {
        return tracker.canCreateTeams();
    }

    public boolean canManageTeams() {
        return tracker.canManageTeams();
    }
}