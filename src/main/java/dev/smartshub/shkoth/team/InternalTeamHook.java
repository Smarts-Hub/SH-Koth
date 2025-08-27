package dev.smartshub.shkoth.team;


import dev.smartshub.shkoth.api.team.KothTeam;
import dev.smartshub.shkoth.api.team.Team;
import dev.smartshub.shkoth.api.team.handle.TeamHandler;
import dev.smartshub.shkoth.api.team.hook.TeamHook;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

public class InternalTeamHook implements TeamHook, TeamHandler {

    private final Set<Team> teams = new CopyOnWriteArraySet<>();

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
        return -1;
    }

   @Override
    public Set<UUID> getTeamMembers(UUID anyTeamMember) {
        return findTeamByMember(anyTeamMember)
                .map(Team::getMembers)
                .orElse(Set.of());
    }

    @Override
    public UUID getTeamLeader(UUID anyTeamMember) {
        return findTeamByMember(anyTeamMember)
                .map(Team::getLeader)
                .orElse(null);
    }

    @Override
    public String getTeamDisplayName(UUID anyTeamMember) {
        return findTeamByMember(anyTeamMember)
                .map(Team::getDisplayName)
                .orElse("No Team");
    }

    @Override
    public boolean isTeamMember(UUID uuid) {
        return findTeamByMember(uuid).isPresent();
    }

    @Override
    public boolean isTeamLeader(UUID uuid) {
        return teams.stream().anyMatch(t -> t.getLeader().equals(uuid));
    }

    @Override
    public boolean areTeammates(UUID player1, UUID player2) {
        return findTeamByMember(player1)
                .map(team -> team.contains(player2))
                .orElse(false);
    }


    @Override
    public KothTeam createTeam(UUID leader) {
        if (isTeamLeader(leader)) {
            throw new IllegalStateException("El líder ya tiene un equipo");
        }
        Team team = Team.withLeader(leader);
        teams.add(team);
        return team;
    }

    @Override
    public boolean addMemberToTeam(UUID member, UUID teamLeader) {
        Optional<Team> optTeam = findTeamByLeader(teamLeader);
        if (optTeam.isEmpty()) return false;

        Team team = optTeam.get();
        Team updated = team.addMember(member);
        if (updated.equals(team)) return false;

        teams.remove(team);
        teams.add(updated);
        return true;
    }

    @Override
    public boolean removeMemberFromTeam(UUID member) {
        Optional<Team> optTeam = findTeamByMember(member);
        if (optTeam.isEmpty()) return false;

        Team team = optTeam.get();
        Team updated = team.removeMember(member);
        if (updated == null) {
            return false;
        }

        teams.remove(team);
        teams.add(updated);
        return true;
    }

    @Override
    public boolean disbandTeam(UUID leader) {
        return findTeamByLeader(leader)
                .map(teams::remove)
                .orElse(false);
    }

    @Override
    public boolean transferLeadership(UUID oldLeader, UUID newLeader) {
        Optional<Team> optTeam = findTeamByLeader(oldLeader);
        if (optTeam.isEmpty()) return false;

        Team team = optTeam.get();
        if (!team.getMembers().contains(newLeader)) {
            return false;
        }

        Team updated = team.changeLeader(newLeader);
        teams.remove(team);
        teams.add(updated);
        return true;
    }

    private Optional<Team> findTeamByMember(UUID member) {
        return teams.stream()
                .filter(t -> t.contains(member))
                .findFirst();
    }

    private Optional<Team> findTeamByLeader(UUID leader) {
        return teams.stream()
                .filter(t -> t.getLeader().equals(leader))
                .findFirst();
    }
}
