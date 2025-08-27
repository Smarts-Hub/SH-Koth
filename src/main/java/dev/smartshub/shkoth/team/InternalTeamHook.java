package dev.smartshub.shkoth.team;

import dev.smartshub.shkoth.api.team.KothTeam;
import dev.smartshub.shkoth.api.team.Team;
import dev.smartshub.shkoth.api.team.hook.TeamHook;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InternalTeamHook implements TeamHook {
    private final Set<Team> teams = ConcurrentHashMap.newKeySet();

    @Override
    public String getPluginName() {
        return "Internal";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public KothTeam getTeamFrom(UUID playerId) {
        return teams.stream()
                .filter(team -> team.contains(playerId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Set<UUID> getTeamMembers(UUID anyTeamMember) {
        KothTeam team = getTeamFrom(anyTeamMember);
        return team != null ? new HashSet<>(team.getMembers()) : Set.of();
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
    public boolean isTeamMember(UUID uuid) {
        return teams.stream().anyMatch(team -> team.contains(uuid));
    }

    @Override
    public boolean isTeamLeader(UUID uuid) {
        return teams.stream().anyMatch(team -> team.isLeader(uuid));
    }

    @Override
    public boolean areTeammates(UUID player1, UUID player2) {
        if (player1.equals(player2)) return true;
        KothTeam team = getTeamFrom(player1);
        return team != null && team.contains(player2);
    }

    public KothTeam createTeam(UUID leader) {
        if (getTeamByLeader(leader).isPresent()) {
            throw new IllegalArgumentException("Player is already a team leader");
        }
        if (getTeamFrom(leader) != null) {
            throw new IllegalArgumentException("Player is already in a team");
        }
        Team newTeam = Team.withLeader(leader);
        teams.add(newTeam);
        return newTeam;
    }

    public boolean addMemberToTeam(UUID member, UUID teamLeader) {
        Optional<KothTeam> teamOpt = getTeamByLeader(teamLeader);
        if (teamOpt.isEmpty()) return false;

        Team currentTeam = (Team) teamOpt.get();

        Team memberCurrentTeam = (Team) getTeamFrom(member);
        if (memberCurrentTeam != null) {
            Team updatedOldTeam = memberCurrentTeam.removeMember(member);
            if (updatedOldTeam == null) {
                teams.removeIf(team -> team.getLeader().equals(memberCurrentTeam.getLeader()));
            } else {
                updateTeam(updatedOldTeam);
            }
        }

        Team updatedTeam = currentTeam.addMember(member);
        updateTeam(updatedTeam);
        return true;
    }

    public boolean removeMemberFromTeam(UUID member) {
        Team currentTeam = (Team) getTeamFrom(member);
        if (currentTeam == null) return false;

        Team updatedTeam = currentTeam.removeMember(member);
        if (updatedTeam == null) {
            teams.removeIf(team -> team.getLeader().equals(currentTeam.getLeader()));
        } else {
            updateTeam(updatedTeam);
        }
        return true;
    }

    public boolean disbandTeam(UUID leader) {
        return teams.removeIf(team -> team.getLeader().equals(leader));
    }

    public boolean transferLeadership(UUID oldLeader, UUID newLeader) {
        Optional<KothTeam> teamOpt = getTeamByLeader(oldLeader);
        if (teamOpt.isEmpty()) return false;

        Team currentTeam = (Team) teamOpt.get();
        if (!currentTeam.contains(newLeader)) return false;

        Team updatedTeam = currentTeam.changeLeader(newLeader);
        teams.removeIf(team -> team.getLeader().equals(oldLeader));
        teams.add(updatedTeam);
        return true;
    }

    private void updateTeam(Team updatedTeam) {
        teams.removeIf(team -> team.getLeader().equals(updatedTeam.getLeader()));
        teams.add(updatedTeam);
    }

    public void clearAllTeams() {
        teams.clear();
    }

    public int getTeamCount() {
        return teams.size();
    }
}