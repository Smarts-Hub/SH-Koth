package dev.smartshub.shkoth.team.tracker;

import dev.smartshub.shkoth.api.team.Team;
import dev.smartshub.shkoth.api.team.TeamTracker;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalTeamTracker implements TeamTracker {

    private static GlobalTeamTracker instance;
    private final Set<Team> teams = ConcurrentHashMap.newKeySet();

    private GlobalTeamTracker() {}

    // Im not proud of this static singleton implementation, but its fine for now
    public static GlobalTeamTracker getInstance() {
        if (instance == null) {
            synchronized (GlobalTeamTracker.class) {
                if (instance == null) {
                    instance = new GlobalTeamTracker();
                }
            }
        }
        return instance;
    }

    @Override
    public Team getTeamFrom(UUID uuid) {
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

    @Override
    public void addMember(UUID uuid, Team team) {
        Team currentTeam = getTeamFrom(uuid);
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

    @Override
    public void removeMember(UUID uuid) {
        Team currentTeam = getTeamFrom(uuid);
        if (currentTeam == null) return;
        Team updatedTeam = currentTeam.removeMember(uuid);
        if (updatedTeam == null) {
            dissolveTeam(currentTeam.leader());
        } else {
            updateTeam(updatedTeam);
        }
    }

    @Override
    public Team updateLeader(UUID oldLeader, UUID newLeader) {
        Team current = getTeamByLeader(oldLeader).orElseThrow(() -> new IllegalArgumentException("Team not found"));
        if (oldLeader.equals(newLeader)) return current;
        if (!current.contains(newLeader)) throw new IllegalArgumentException("New leader must be a team member");
        Team updated = current.changeLeader(newLeader);
        teams.removeIf(t -> t.leader().equals(oldLeader));
        teams.add(updated);
        return updated;
    }

    private void updateTeam(Team updatedTeam) {
        teams.removeIf(team -> team.leader().equals(updatedTeam.leader()));
        teams.add(updatedTeam);
    }

    @Override
    public void clearTeam(UUID uuid) {
        Team team = getTeamFrom(uuid);
        if (team != null) dissolveTeam(team.leader());
    }

    @Override
    public void clearAllTeams() {
        teams.clear();
    }

    @Override
    public Set<UUID> getTeamMembers(UUID anyTeamMember) {
        Team team = getTeamFrom(anyTeamMember);
        return team != null ? new HashSet<>(team.members()) : Set.of();
    }

    @Override
    public boolean areTeammates(UUID player1, UUID player2) {
        if (player1.equals(player2)) return true;
        Team team1 = getTeamFrom(player1);
        return team1 != null && team1.contains(player2);
    }

    @Override
    public Collection<Team> getAllTeams() {
        return new HashSet<>(teams);
    }

    @Override
    public Optional<Team> getTeamByLeader(UUID leader) {
        return teams.stream().filter(team -> team.leader().equals(leader)).findFirst();
    }

    @Override
    public Team createTeam(UUID leader) {
        if (getTeamByLeader(leader).isPresent()) {
            throw new IllegalArgumentException("Player is already a team leader");
        }
        Team existingTeam = getTeamFrom(leader);
        if (existingTeam != null) {
            throw new IllegalArgumentException("Player is already in a team");
        }
        Team newTeam = Team.withLeader(leader);
        teams.add(newTeam);
        return newTeam;
    }

    @Override
    public void dissolveTeam(UUID leader) {
        teams.removeIf(team -> team.leader().equals(leader));
    }

    @Override
    public String getTeamDisplayName(Team team) {
        Player leader = team.getLeaderPlayer();
        String leaderName = leader != null ? leader.getName() : "Unknown";
        return team.members().size() == 1
                ? leaderName
                : leaderName + "'s Team (" + team.members().size() + ")";
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