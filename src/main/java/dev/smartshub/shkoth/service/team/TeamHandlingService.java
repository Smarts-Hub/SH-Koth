package dev.smartshub.shkoth.service.team;

import dev.smartshub.shkoth.api.team.KothTeam;
import dev.smartshub.shkoth.api.team.TeamWrapper;
import dev.smartshub.shkoth.service.notify.NotifyService;
import dev.smartshub.shkoth.team.ContextualTeamTracker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamHandlingService {
    
    private final ContextualTeamTracker teamTracker;
    private final NotifyService notifyService;
    
    public TeamHandlingService(NotifyService notifyService, ContextualTeamTracker teamTracker) {
        this.notifyService = notifyService;
        this.teamTracker = teamTracker;
    }
    
    public void createTeam(Player leader, int maxMembers) {
        UUID leaderId = leader.getUniqueId();
        
        if (hasTeam(leaderId)) {
            notifyService.sendChat(leader, "team.already-in-a-team");
            return;
        }

        // Silent fail, as this command should not be available due to existing external team plugin
        if (!teamTracker.getActiveProvider().equals("Internal")) return;
        
        TeamWrapper team = teamTracker.createInternalTeam(leaderId, maxMembers);
        if (team == null) {
            notifyService.sendChat(leader, "team.cant-create-team");
            return;
        }

        notifyService.sendChat(leader, "team.team-created-success");
    }

    public void joinTeam(Player player, UUID teamLeader) {
        UUID playerId = player.getUniqueId();
        
        if (hasTeam(playerId)) {
            notifyService.sendChat(player, "team.already-in-a-team");
            return;
        }

        // Silent fail, as this command should not be available due to existing external team plugin
        if (!teamTracker.getActiveProvider().equals("Internal")) return;
        
        boolean success = teamTracker.getInternalHandler().addMemberToTeam(playerId, teamLeader);

        if(!success){
            notifyService.sendChat(player, "team.cant-join-team");
            return;
        }

        notifyService.sendChat(player, "team.joined-team-success");
    }
    
    public void leaveTeam(Player player) {
        UUID playerId = player.getUniqueId();
        
        if (!hasTeam(playerId)) {
            notifyService.sendChat(player, "team.not-in-a-team");
            return;
        }

        // Silent fail, as this command should not be available due to existing external team plugin
        if (!teamTracker.getActiveProvider().equals("Internal")) return;
        
        if (isTeamLeader(playerId)) {
            notifyService.sendChat(player, "team.leader-cant-leave");
            return;
        }
        
        boolean success = teamTracker.getInternalHandler().removeMemberFromTeam(playerId);

        if(!success){
            notifyService.sendChat(player, "team.cant-leave-team");
            return;
        }

        notifyService.sendChat(player, "team.left-team-success");
    }
    
    public void disbandTeam(Player leader) {
        UUID leaderId = leader.getUniqueId();
        
        if (!isTeamLeader(leaderId)) {
            notifyService.sendChat(leader, "team.only-leader-can-disband");
            return;
        }

        // Silent fail, as this command should not be available due to existing external team plugin
        if (!teamTracker.getActiveProvider().equals("Internal")) return;
        
        boolean success = teamTracker.getInternalHandler().disbandTeam(leaderId);

        if(!success){
            notifyService.sendChat(leader, "team.cant-disband-team");
            return;
        }

        notifyService.sendChat(leader, "team.disbanded-team-success");
    }
    
    public void kickMember(Player leader, String toKickName) {
        Player toKick = Bukkit.getPlayer(toKickName);
        if (toKick == null) {
            notifyService.sendChat(leader, "team.player-not-found");
            return;
        }

        UUID leaderId = leader.getUniqueId();
        UUID memberToKick = toKick.getUniqueId();

        if (!isTeamLeader(leaderId)) {
            notifyService.sendChat(leader, "team.only-leader-can-kick");
            return;
        }
        
        if (leaderId.equals(memberToKick)) {
            notifyService.sendChat(leader, "team.cant-kick-yourself");
            return;
        }
        
        if (!teamTracker.areTeammates(leaderId, memberToKick)) {
            notifyService.sendChat(leader, "team.player-not-in-your-team");
            return;
        }

        // Silent fail, as this command should not be available due to existing external team plugin
        if (!teamTracker.getActiveProvider().equals("Internal")) return;
        
        boolean success = teamTracker.getInternalHandler().removeMemberFromTeam(memberToKick);

        if(!success){
            notifyService.sendChat(leader, "team.cant-kick-member");
            return;
        }
        notifyService.sendChat(leader, "team.kicked-member-success");
    }
    
    public void transferLeadership(Player currentLeader, String newLeaderName) {
        Player newLeader = Bukkit.getPlayer(newLeaderName);
        if (newLeader == null) {
            notifyService.sendChat(currentLeader, "team.player-not-found");
            return;
        }

        UUID currentLeaderId = currentLeader.getUniqueId();
        UUID newLeaderId = newLeader.getUniqueId();

        if (!isTeamLeader(currentLeaderId)) {
            notifyService.sendChat(currentLeader, "team.only-leader-can-transfer");
            return;
        }
        
        if (!teamTracker.areTeammates(currentLeaderId, newLeaderId)) {
            notifyService.sendChat(currentLeader, "team.player-not-in-your-team");
            return;
        }

        // Silent fail, as this command should not be available due to existing external team plugin
        if (!teamTracker.getActiveProvider().equals("Internal")) return;
        
        boolean success = teamTracker.getInternalHandler().transferLeadership(currentLeaderId, newLeaderId);

        if(!success){
            notifyService.sendChat(currentLeader, "team.cant-transfer-leadership");
            return;
        }
        notifyService.sendChat(currentLeader, "team.transferred-leadership-success");
        notifyService.sendChat(newLeader, "team.you-are-leader-now");
    }
    
    public KothTeam getPlayerTeam(UUID playerId) {
        return teamTracker.getTeamWrapper(playerId);
    }
    
    public boolean hasTeam(UUID playerId) {
        return teamTracker.isTeamMember(playerId);
    }
    
    public boolean isTeamLeader(UUID playerId) {
        return teamTracker.isTeamLeader(playerId);
    }
}