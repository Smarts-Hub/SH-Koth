package dev.smartshub.shkoth.service.team;

import dev.smartshub.shkoth.api.team.Team;
import dev.smartshub.shkoth.service.notify.NotifyService;
import dev.smartshub.shkoth.team.tracker.GlobalTeamTracker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class TeamService {

    private final NotifyService notifyService;
    private final GlobalTeamTracker tracker = GlobalTeamTracker.getInstance();
    private final Map<UUID, UUID> pendingInvites = new HashMap<>(); // target -> leader

    public TeamService(NotifyService notifyService) {
        this.notifyService = notifyService;
    }

    public Team createTeam(Player leader) {
        notifyService.sendChat(leader, "team.created");
        return tracker.createTeam(leader.getUniqueId());
    }

    public void disbandTeam(Player leader) {
        Team team = tracker.getTeamFrom(leader.getUniqueId());
        team.getOnlineMembers().forEach(player -> notifyService.sendChat(player, "team.disbanded"));
        tracker.dissolveTeam(leader.getUniqueId());
    }

    public void leaveTeam(Player player) {
        tracker.removeMember(player.getUniqueId());
        notifyService.sendChat(player, "team.left");
    }

    public void kickMember(Player kicker, String target) {
        Team team = tracker.getTeamFrom(kicker.getUniqueId());
        if (team == null || !team.isLeader(kicker.getUniqueId())) {
            notifyService.sendChat(kicker, "team.not-leader");
            return;
        }
        Player targetPlayer = Bukkit.getPlayerExact(target);
        if (targetPlayer == null || !team.contains(targetPlayer.getUniqueId())) {
            notifyService.sendChat(kicker, "team.not-a-member");
            return;
        }
        tracker.removeMember(targetPlayer.getUniqueId());
        notifyService.sendChat(kicker, "team.kicked");
    }

    public void setLeader(Player currentLeader, String newLeaderName) {
        Team team = tracker.getTeamFrom(currentLeader.getUniqueId());
        if (team == null || !team.isLeader(currentLeader.getUniqueId())) {
            notifyService.sendChat(currentLeader, "team.not-leader");
            return;
        }
        Player newLeader = Bukkit.getPlayerExact(newLeaderName);
        if (newLeader == null || !team.contains(newLeader.getUniqueId())) {
            notifyService.sendChat(currentLeader, "team.not-a-member");
            return;
        }
        tracker.updateLeader(currentLeader.getUniqueId(), newLeader.getUniqueId());
        tracker.getTeamFrom(newLeader.getUniqueId()).getOnlineMembers()
                .forEach(player -> notifyService.sendChat(player, "team.new-leader"));
    }

    public void invite(Player leader, String target) {
        Team team = tracker.getTeamFrom(leader.getUniqueId());
        if (team == null || !team.isLeader(leader.getUniqueId())) {
            notifyService.sendChat(leader, "team.not-leader");
            return;
        }
        Player targetPlayer = Bukkit.getPlayerExact(target);
        if (targetPlayer == null) {
            notifyService.sendChat(leader, "player-not-found");
            return;
        }
        pendingInvites.put(targetPlayer.getUniqueId(), leader.getUniqueId());
        tracker.getTeamFrom(leader.getUniqueId()).getOnlineMembers()
                .forEach(player -> notifyService.sendChat(player, "team.invite-sent"));
    }

    public void acceptInvite(Player player) {
        UUID leaderId = pendingInvites.remove(player.getUniqueId());
        if (leaderId == null) return;

        Team team = tracker.getTeamByLeader(leaderId).orElse(null);
        if (team == null) return;

        tracker.addMember(player.getUniqueId(), team);
        team.getOnlineMembers().forEach(member -> notifyService.sendChat(member, "team.new-member"));
    }

    public void sendTeamMessage(Player sender, String message) {
        Team team = tracker.getTeamFrom(sender.getUniqueId());
        if (team == null) {
            notifyService.sendChat(sender, "team.no-team");
            return;
        }
        // Example formatting, customize should be implemented
        String formatted = "§a[Team] " + sender.getName() + ": §f" + message;
        for (UUID memberId : team.members()) {
            Player member = Bukkit.getPlayer(memberId);
            if (member != null && member.isOnline()) {
                member.sendMessage(formatted);
            }
        }
    }

    public void sendTeamInfo(Player player){
        Team team = tracker.getTeamFrom(player.getUniqueId());
        if(team == null){
            notifyService.sendChat(player, "team.no-team");
            return;
        }
        team.getOnlineMembers().forEach(p -> {
            if(team.isLeader(p.getUniqueId())){
                notifyService.sendChat(player, "team.info.leader");
            } else {
                notifyService.sendChat(player, "team.info.member");
            }
        });
    }

    public void declineInvite(Player player) {
        tracker.getTeamFrom(pendingInvites.get(player.getUniqueId())).getOnlineMembers()
                .forEach(p -> notifyService.sendChat(p, "team.invite-declined"));
        pendingInvites.remove(player.getUniqueId());
    }

    public Team getTeam(Player player) {
        return tracker.getTeamFrom(player.getUniqueId());
    }

    public Collection<Team> listTeams() {
        return tracker.getAllTeams();
    }
}
