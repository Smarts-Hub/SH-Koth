package dev.smartshub.shkoth.team;

import dev.smartshub.shkoth.api.team.InternalKothTeam;
import dev.smartshub.shkoth.api.team.KothTeam;
import dev.smartshub.shkoth.api.team.handle.TeamHandler;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InternalTeamHandler implements TeamHandler {
    private final Map<UUID, InternalKothTeam> teams = new ConcurrentHashMap<>();
    private final Map<UUID, UUID> playerToTeam = new ConcurrentHashMap<>();
    
    @Override
    public KothTeam createTeam(UUID leader) {
        return createTeam(leader, Integer.MAX_VALUE);
    }
    
    public InternalKothTeam createTeam(UUID leader, int maxMembers) {
        removeMemberFromTeam(leader);
        
        InternalKothTeam team = new InternalKothTeam(leader, maxMembers);
        teams.put(team.getTeamId(), team);
        playerToTeam.put(leader, team.getTeamId());
        
        return team;
    }
    
    @Override
    public boolean addMemberToTeam(UUID member, UUID teamLeader) {
        InternalKothTeam team = getTeamByLeader(teamLeader);
        if (team == null) return false;
        
        removeMemberFromTeam(member);
        
        if (team.addMember(member)) {
            playerToTeam.put(member, team.getTeamId());
            return true;
        }
        return false;
    }
    
    @Override
    public boolean removeMemberFromTeam(UUID member) {
        UUID teamId = playerToTeam.get(member);
        if (teamId == null) return false;
        
        InternalKothTeam team = teams.get(teamId);
        if (team == null) return false;
        
        if (team.isLeader(member)) {
            return disbandTeam(member);
        } else {
            team.removeMember(member);
            playerToTeam.remove(member);
            return true;
        }
    }
    
    @Override
    public boolean disbandTeam(UUID leader) {
        InternalKothTeam team = getTeamByLeader(leader);
        if (team == null) return false;
        
        team.getMembers().forEach(playerToTeam::remove);
        teams.remove(team.getTeamId());
        
        return true;
    }
    
    @Override
    public boolean transferLeadership(UUID oldLeader, UUID newLeader) {
        InternalKothTeam team = getTeamByLeader(oldLeader);
        if (team == null) return false;
        
        return team.transferLeadership(newLeader);
    }
    
    public InternalKothTeam getTeam(UUID playerId) {
        UUID teamId = playerToTeam.get(playerId);
        return teamId != null ? teams.get(teamId) : null;
    }
    
    public Set<UUID> getTeamMembers(UUID playerId) {
        InternalKothTeam team = getTeam(playerId);
        return team != null ? team.getMembers() : Set.of();
    }
    
    public boolean isTeamMember(UUID uuid) {
        return playerToTeam.containsKey(uuid);
    }
    
    public boolean isTeamLeader(UUID uuid) {
        InternalKothTeam team = getTeam(uuid);
        return team != null && team.isLeader(uuid);
    }
    
    public boolean areTeammates(UUID player1, UUID player2) {
        UUID team1 = playerToTeam.get(player1);
        UUID team2 = playerToTeam.get(player2);
        return team1 != null && team1.equals(team2);
    }
    
    public String getTeamDisplayName(UUID playerId) {
        InternalKothTeam team = getTeam(playerId);
        return team != null ? team.getDisplayName() : "No Team";
    }

    @Override
    public void updateTeams() {
        teams.entrySet().removeIf(entry -> {
            InternalKothTeam team = entry.getValue();
            if (team.getMembers().isEmpty()) {
                return true;
            }
            return false;
        });

        playerToTeam.entrySet().removeIf(entry -> {
            UUID teamId = entry.getValue();
            return !teams.containsKey(teamId);
        });
    }

    
    private InternalKothTeam getTeamByLeader(UUID leader) {
        return teams.values().stream()
                .filter(team -> team.isLeader(leader))
                .findFirst()
                .orElse(null);
    }

    public Collection<InternalKothTeam> getAllTeams() {
        return teams.values();
    }
}