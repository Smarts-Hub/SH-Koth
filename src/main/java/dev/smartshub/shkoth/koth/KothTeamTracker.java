package dev.smartshub.shkoth.koth;

import dev.smartshub.shkoth.api.model.team.Team;
import dev.smartshub.shkoth.api.model.team.TeamTracker;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class KothTeamTracker implements TeamTracker {

    private final int maxTeamSize;

    public KothTeamTracker(int maxTeamSize) {
        this.maxTeamSize = maxTeamSize;
    }

    // Redundant? Yes, but simplifies and increase logic speed
    private final Map<UUID, Team> playerToTeam = new ConcurrentHashMap<>();
    private final Map<UUID, Team> leaderToTeam = new ConcurrentHashMap<>();

    @Override
    public Team getTeamFrom(UUID uuid) {
        return playerToTeam.get(uuid);
    }

    @Override
    public boolean isTeamMember(UUID uuid) {
        return playerToTeam.containsKey(uuid);
    }

    @Override
    public boolean isTeamLeader(UUID uuid) {
        Team team = playerToTeam.get(uuid);
        return team != null && team.isLeader(uuid);
    }

    @Override
    public void addMember(UUID uuid, Team team) {
        Team currentTeam = playerToTeam.get(uuid);

        if (currentTeam != null) {
            Team updatedOldTeam = currentTeam.removeMember(uuid);
            if (updatedOldTeam == null) {
                dissolveTeam(currentTeam.leader());
            } else {
                updateTeam(updatedOldTeam);
            }
        }

        Team updatedNewTeam = team.addMember(uuid);
        updateTeam(updatedNewTeam);
    }

    @Override
    public void removeMember(UUID uuid) {
        Team currentTeam = playerToTeam.get(uuid);
        if (currentTeam == null) return;

        Team updatedTeam = currentTeam.removeMember(uuid);
        if (updatedTeam == null) {
            dissolveTeam(currentTeam.leader());
        } else {
            updateTeam(updatedTeam);
        }
    }

    private void updateTeam(Team team) {
        for (UUID member : team.members()) {
            playerToTeam.put(member, team);
        }
        leaderToTeam.put(team.leader(), team);
    }

    @Override
    public void clearTeam(UUID uuid) {
        Team team = playerToTeam.get(uuid);
        if (team != null) {
            dissolveTeam(team.leader());
        }
    }

    @Override
    public void clearAllTeams() {
        playerToTeam.clear();
        leaderToTeam.clear();
    }

    @Override
    public Set<UUID> getTeamMembers(UUID anyTeamMember) {
        Team team = getTeamFrom(anyTeamMember);
        return team != null ? new HashSet<>(team.members()) : Set.of();
    }

    @Override
    public boolean areTeammates(UUID player1, UUID player2) {
        Team team1 = getTeamFrom(player1);
        Team team2 = getTeamFrom(player2);
        return team1 != null && team1.equals(team2);
    }

    @Override
    public Collection<Team> getAllTeams() {
        return leaderToTeam.values();
    }

    @Override
    public Optional<Team> getTeamByLeader(UUID leader) {
        return Optional.ofNullable(leaderToTeam.get(leader));
    }

    @Override
    public Team createTeam(UUID leader) {
        Team newTeam = Team.of(leader);
        playerToTeam.put(leader, newTeam);
        leaderToTeam.put(leader, newTeam);
        return newTeam;
    }

    @Override
    public void dissolveTeam(UUID leader) {
        Team team = leaderToTeam.get(leader);
        if (team == null) return;

        for (UUID member : team.members()) {
            playerToTeam.remove(member);
        }

        leaderToTeam.remove(leader);
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
}
