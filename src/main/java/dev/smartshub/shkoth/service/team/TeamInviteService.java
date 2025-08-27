package dev.smartshub.shkoth.service.team;

import dev.smartshub.shkoth.service.notify.NotifyService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TeamInviteService {
    
    private final TeamHandlingService teamService;
    private final NotifyService notifyService;
    private final Map<UUID, TeamInvitation> pendingInvitations = new ConcurrentHashMap<>();
    private final long invitationExpirationTime = 180000; // 180 seconds
    
    public TeamInviteService(TeamHandlingService teamService, NotifyService notifyService) {
        this.teamService = teamService;
        this.notifyService = notifyService;
    }
    
    public void sendInvitation(Player leader, Player target) {
        UUID leaderId = leader.getUniqueId();
        UUID targetId = target.getUniqueId();
        
        if (!teamService.isTeamLeader(leaderId)) {
            notifyService.sendChat(leader, "team.not-team-leader");
            return;
        }
        
        if (teamService.hasTeam(targetId)) {
            notifyService.sendChat(leader, "team.target-already-in-a-team");
            return;
        }
        
        if (hasPendingInvitation(targetId)) {
            notifyService.sendChat(leader, "team.target-already-invited");
            return;
        }
        
        TeamInvitation invitation = new TeamInvitation(leaderId, targetId);
        pendingInvitations.put(targetId, invitation);

        //TODO: use args here!!! leader.getName() and then target.getName()
        notifyService.sendChat(target, "team.invitation-received");
        notifyService.sendChat(leader, "team.invitation-sent");
    }
    
    public void acceptInvitation(Player player) {
        UUID playerId = player.getUniqueId();
        TeamInvitation invitation = pendingInvitations.get(playerId);
        
        if (invitation == null || invitation.isExpired(invitationExpirationTime)) {
            pendingInvitations.remove(playerId);
            notifyService.sendChat(player, "team.no-pending-invitations");
            return;
        }
        
        pendingInvitations.remove(playerId);
        
        Player leader = Bukkit.getPlayer(invitation.getLeaderId());
        if (leader != null) {
            notifyService.sendChat(leader, "team.invitation-accepted");
        }
        
        teamService.joinTeam(player, invitation.getLeaderId());
    }
    
    public void declineInvitation(Player player) {
        UUID playerId = player.getUniqueId();
        TeamInvitation invitation = pendingInvitations.remove(playerId);
        
        if (invitation == null) {
            notifyService.sendChat(player, "team.no-pending-invitations");
            return;
        }
        
        Player leader = Bukkit.getPlayer(invitation.getLeaderId());
        if (leader != null) {
            //TODO: use args for player.getName()
            notifyService.sendChat(leader, "team.invitation-declined");
        }

        notifyService.sendChat(player, "team.invitation-declined-success");
    }
    
    public boolean hasPendingInvitation(UUID playerId) {
        TeamInvitation invitation = pendingInvitations.get(playerId);
        if (invitation != null && invitation.isExpired(invitationExpirationTime)) {
            pendingInvitations.remove(playerId);
            return false;
        }
        return invitation != null;
    }
    
    public void cleanupExpiredInvitations() {
        pendingInvitations.entrySet().removeIf(entry -> 
            entry.getValue().isExpired(invitationExpirationTime)
        );
    }
    
    private static class TeamInvitation {
        private final UUID leaderId;
        private final UUID invitedPlayerId;
        private final long timestamp;
        
        public TeamInvitation(UUID leaderId, UUID invitedPlayerId) {
            this.leaderId = leaderId;
            this.invitedPlayerId = invitedPlayerId;
            this.timestamp = System.currentTimeMillis();
        }
        
        public UUID getLeaderId() { return leaderId; }
        public UUID getInvitedPlayerId() { return invitedPlayerId; }
        
        public boolean isExpired(long expirationTime) {
            return System.currentTimeMillis() - timestamp > expirationTime;
        }
    }
}