package dev.smartshub.shkoth.team.track;

import dev.smartshub.shkoth.api.team.KothTeam;
import dev.smartshub.shkoth.api.team.Team;
import dev.smartshub.shkoth.api.team.track.TeamTracker;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InternalTeamTracker implements TeamTracker {

    private static InternalTeamTracker instance;
    private final Set<Team> teams = ConcurrentHashMap.newKeySet();

    private InternalTeamTracker() {}

    // Im not proud of this static singleton implementation, but its fine for now
    public static InternalTeamTracker getInstance() {
        if (instance == null) {
            synchronized (InternalTeamTracker.class) {
                if (instance == null) {
                    instance = new InternalTeamTracker();
                }
            }
        }
        return instance;
    }

    @Override
    public KothTeam getTeamFrom(UUID uuid) {
        return teams.stream()
                .filter(team -> team.contains(uuid))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean isTeamMember(UUID uuid) {
        return teams.stream().anyMatch(team -> team.contains(uuid));
    }

    @Override
    public boolean isTeamLeader(UUID uuid) {
        return teams.stream().anyMatch(team -> team.isLeader(uuid));
    }

    public void addMember(UUID uuid, Team team) {
        Team currentTeam = (Team) getTeamFrom(uuid);
        if (currentTeam != null) {
            if (currentTeam.equals(team)) return;
            Team updatedOldTeam = currentTeam.removeMember(uuid);
            if (updatedOldTeam == null) {
                dissolveTeam(currentTeam.leader());
            } else {
                updateTeam(updatedOldTeam);
            }
        }
        if (!team.contains(uuid)) {
            Team updatedNewTeam = team.addMember(uuid);
            updateTeam(updatedNewTeam);
        }
    }

    public void removeMember(UUID uuid) {
        Team currentTeam = (Team) getTeamFrom(uuid);
        if (currentTeam == null) return;
        Team updatedTeam = currentTeam.removeMember(uuid);
        if (updatedTeam == null) {
            dissolveTeam(currentTeam.leader());
        } else {
            updateTeam(updatedTeam);
        }
    }

    public void updateLeader(UUID oldLeader, UUID newLeader) {
        Team current = (Team) getTeamByLeader(oldLeader).orElseThrow(() -> new IllegalArgumentException("Team not found"));
        if (oldLeader.equals(newLeader)) return;
        if (!current.contains(newLeader)) throw new IllegalArgumentException("New leader must be a team member");
        Team updated = current.changeLeader(newLeader);
        teams.removeIf(t -> t.getLeader().equals(oldLeader));
        teams.add(updated);
    }

    private void updateTeam(Team updatedTeam) {
        teams.removeIf(team -> team.getLeader().equals(updatedTeam.getLeader()));
        teams.add(updatedTeam);
    }

    public void clearTeam(UUID uuid) {
        Team team = (Team) getTeamFrom(uuid);
        if (team != null) dissolveTeam(team.getLeader());
    }

    public void clearAllTeams() {
        teams.clear();
    }

    @Override
    public Set<UUID> getTeamMembers(UUID anyTeamMember) {
        KothTeam team = getTeamFrom(anyTeamMember);
        return team != null ? new HashSet<>(team.getMembers()) : Set.of();
    }

    @Override
    public boolean areTeammates(UUID player1, UUID player2) {
        if (player1.equals(player2)) return true;
        KothTeam team1 = getTeamFrom(player1);
        return team1 != null && team1.contains(player2);
    }

    @Override
    public String getTeamDisplayName(KothTeam team) {
        return team.getDisplayName();
    }

    @Override
    public Collection<KothTeam> getAllTeams() {
        return new HashSet<>(teams);
    }

    @Override
    public Optional<KothTeam> getTeamByLeader(UUID leader) {
        return teams.stream()
                .filter(team -> team.getLeader().equals(leader))
                .map(team -> (KothTeam) team)
                .findFirst();
    }

    @Override
    public KothTeam createTeam(UUID leader) {
        if (getTeamByLeader(leader).isPresent()) {
            throw new IllegalArgumentException("Player is already a team leader");
        }
        KothTeam existingTeam = getTeamFrom(leader);
        if (existingTeam != null) {
            throw new IllegalArgumentException("Player is already in a team");
        }
        Team newTeam = Team.withLeader(leader);
        teams.add(newTeam);
        return newTeam;
    }

    public void dissolveTeam(UUID leader) {
        teams.removeIf(team -> team.getLeader().equals(leader));
    }

    public void cleanupInvalidTeams() {
        teams.removeIf(team -> !team.isValid());
    }

    public int getActiveTeamsCount() {
        return teams.size();
    }

    public boolean hasActiveTeams() {
        return !teams.isEmpty();
    }
}