package dev.smartshub.shkoth.team.track;

import dev.smartshub.shkoth.api.team.KothTeam;
import dev.smartshub.shkoth.api.team.Team;
import dev.smartshub.shkoth.api.team.track.TeamTracker;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InternalTeamTracker implements TeamTracker {

    //TODO: Implement team tracking logic

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
        return null;
    }

    @Override
    public Set<UUID> getTeamMembers(UUID anyTeamMember) {
        return Set.of();
    }

    @Override
    public Collection<KothTeam> getAllTeams() {
        return List.of();
    }

    @Override
    public Optional<KothTeam> getTeamByLeader(UUID leader) {
        return Optional.empty();
    }

    @Override
    public boolean isTeamMember(UUID uuid) {
        return false;
    }

    @Override
    public boolean isTeamLeader(UUID uuid) {
        return false;
    }

    @Override
    public boolean areTeammates(UUID player1, UUID player2) {
        return false;
    }

    @Override
    public KothTeam createTeam(UUID leader) {
        return null;
    }

    @Override
    public boolean canCreateTeams() {
        return false;
    }

    @Override
    public boolean canManageTeams() {
        return false;
    }

    @Override
    public boolean addMemberToTeam(UUID member, UUID teamLeader) {
        return false;
    }

    @Override
    public boolean removeMemberFromTeam(UUID member) {
        return false;
    }

    @Override
    public boolean disbandTeam(UUID leader) {
        return false;
    }

    @Override
    public boolean transferLeadership(UUID oldLeader, UUID newLeader) {
        return false;
    }

    @Override
    public String getTeamDisplayName(KothTeam team) {
        return "";
    }

    @Override
    public String getActiveProvider() {
        return "";
    }
}