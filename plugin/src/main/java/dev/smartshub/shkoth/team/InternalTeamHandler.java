package dev.smartshub.shkoth.team;

import dev.smartshub.shkoth.api.event.dispatcher.TeamEventDispatcher;
import dev.smartshub.shkoth.api.team.InternalKothTeam;
import dev.smartshub.shkoth.api.team.KothTeam;
import dev.smartshub.shkoth.api.team.handle.TeamHandler;
import dev.smartshub.shkoth.api.event.team.MemberLeavedTeamEvent;
import dev.smartshub.shkoth.api.event.team.TeamCreatedEvent;
import dev.smartshub.shkoth.api.event.team.TeamDissolvedEvent;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InternalTeamHandler implements TeamHandler {
    private final Map<UUID, InternalKothTeam> teams = new ConcurrentHashMap<>();
    private final Map<UUID, UUID> playerToTeam = new ConcurrentHashMap<>();
    private final Map<UUID, UUID> leaderToTeamId = new ConcurrentHashMap<>();
    private final TeamEventDispatcher eventDispatcher = new TeamEventDispatcher();

    @Override
    public KothTeam createTeam(UUID leader) {
        return createTeam(leader, Integer.MAX_VALUE);
    }

    public synchronized InternalKothTeam createTeam(UUID leader, int maxMembers) {
        // If the player is already in a team, remove them first.
        if (playerToTeam.containsKey(leader)) {
            removeMemberFromTeam(leader);
        }

        InternalKothTeam team = new InternalKothTeam(leader, maxMembers);
        teams.put(team.getTeamId(), team);
        playerToTeam.put(leader, team.getTeamId());
        leaderToTeamId.put(leader, team.getTeamId());

        // Fire the event after the team has been successfully created
        eventDispatcher.fireTeamCreatedEvent(team, TeamCreatedEvent.CreationReason.MANUAL);

        return team;
    }

    @Override
    public synchronized boolean addMemberToTeam(UUID member, UUID teamLeader) {
        InternalKothTeam team = getTeamByLeader(teamLeader);
        if (team == null || team.isFull()) {
            return false;
        }

        KothTeam oldTeam = getTeam(member);
        // If the member is in another team, remove them first.
        if (oldTeam != null) {
            removeMemberFromTeam(member);
        }

        if (team.addMember(member)) {
            playerToTeam.put(member, team.getTeamId());
            // Fire event after the member has successfully joined
            eventDispatcher.fireMemberJoinedTeamEvent(oldTeam, team, member, teamLeader);
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean removeMemberFromTeam(UUID member) {
        UUID teamId = playerToTeam.get(member);
        if (teamId == null) {
            return false;
        }

        InternalKothTeam team = teams.get(teamId);
        if (team == null) {
            playerToTeam.remove(member);
            return false;
        }

        if (team.isLeader(member)) {
            return disbandTeam(member);
        } else {
            InternalKothTeam oldTeamState = new InternalKothTeam(team);
            if (team.removeMember(member)) {
                playerToTeam.remove(member);
                eventDispatcher.fireMemberLeavedTeamEvent(oldTeamState, team, member,
                        MemberLeavedTeamEvent.RemovalReason.LEFT_VOLUNTARILY);
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized boolean disbandTeam(UUID leader) {
        InternalKothTeam team = getTeamByLeader(leader);
        if (team == null) {
            return false;
        }

        leaderToTeamId.remove(leader);

        for (UUID memberId : team.getMembers()) {
            playerToTeam.remove(memberId);
            eventDispatcher.fireMemberLeavedTeamEvent(team, null, memberId,
                    MemberLeavedTeamEvent.RemovalReason.TEAM_DISSOLVED);
        }

        teams.remove(team.getTeamId());
        eventDispatcher.fireTeamDissolvedEvent(team, leader, TeamDissolvedEvent.DissolutionReason.MANUAL_DISSOLVE);

        return true;
    }

    @Override
    public synchronized boolean transferLeadership(UUID oldLeader, UUID newLeader) {
        InternalKothTeam team = getTeamByLeader(oldLeader);
        if (team == null) {
            return false;
        }

        if (team.transferLeadership(newLeader)) {
            leaderToTeamId.remove(oldLeader);
            leaderToTeamId.put(newLeader, team.getTeamId());
            eventDispatcher.fireTeamChangeLeaderEvent(team, oldLeader, newLeader);
            return true;
        }
        return false;
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
    public synchronized void updateTeams() {
        teams.entrySet().removeIf(entry -> {
            InternalKothTeam team = entry.getValue();
            if (team.getMembers().isEmpty()) {
                leaderToTeamId.remove(team.getLeader()); // Also clean up leader map
                eventDispatcher.fireTeamDissolvedEvent(team, null, TeamDissolvedEvent.DissolutionReason.EMPTY_TEAM);
                return true;
            }
            return false;
        });

        playerToTeam.entrySet().removeIf(entry -> !teams.containsKey(entry.getValue()));
    }

    private InternalKothTeam getTeamByLeader(UUID leader) {
        UUID teamId = leaderToTeamId.get(leader);
        return teamId != null ? teams.get(teamId) : null;
    }

    public Collection<InternalKothTeam> getAllTeams() {
        return teams.values();
    }
}