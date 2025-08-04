package dev.smartshub.shkoth.koth.track;

import dev.smartshub.shkoth.api.model.team.Team;
import dev.smartshub.shkoth.api.model.team.TeamTracker;
import org.bukkit.entity.Player;

import java.util.*;

public class KothTeamTracker implements TeamTracker {

    private final int maxTeamSize;
    private final Set<Team> teams = new HashSet<>();

    public KothTeamTracker(int maxTeamSize) {
        if (maxTeamSize < 1) {
            throw new IllegalArgumentException("Max team size must be at least 1");
        }
        this.maxTeamSize = maxTeamSize;
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
        return teams.stream()
                .anyMatch(team -> team.contains(uuid));
    }

    @Override
    public boolean isTeamLeader(UUID uuid) {
        return teams.stream()
                .anyMatch(team -> team.isLeader(uuid));
    }

    @Override
    public void addMember(UUID uuid, Team team) {
        Team currentTeam = getTeamFrom(uuid);

        if (team.size() >= maxTeamSize && !team.contains(uuid)) {
            throw new IllegalStateException("Team has reached maximum size of " + maxTeamSize);
        }

        if (currentTeam != null) {
            if (currentTeam.equals(team)) {
                return;
            }

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

    private void updateTeam(Team updatedTeam) {
        teams.removeIf(team -> team.leader().equals(updatedTeam.leader()));
        teams.add(updatedTeam);
    }

    @Override
    public void clearTeam(UUID uuid) {
        Team team = getTeamFrom(uuid);
        if (team != null) {
            dissolveTeam(team.leader());
        }
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
        return teams;
    }

    @Override
    public Optional<Team> getTeamByLeader(UUID leader) {
        return teams.stream()
                .filter(team -> team.leader().equals(leader))
                .findFirst();
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

        if (team.size() == 1) {
            return leaderName;
        } else {
            return leaderName + "'s Team (" + team.size() + ")";
        }
    }

    public void cleanupInvalidTeams() {
        teams.removeIf(team -> !team.isValid());
    }
}
