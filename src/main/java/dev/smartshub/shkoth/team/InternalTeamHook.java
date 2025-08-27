package dev.smartshub.shkoth.team;

import dev.smartshub.shkoth.api.team.KothTeam;
import dev.smartshub.shkoth.api.team.hook.TeamHook;
import dev.smartshub.shkoth.team.track.InternalTeamTracker;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class InternalTeamHook implements TeamHook {
    
    private final InternalTeamTracker internalTracker;
    
    public InternalTeamHook(InternalTeamTracker internalTracker) {
        this.internalTracker = internalTracker;
    }

    @Override
    public String getPluginName() {
        return "Internal";
    }

    @Override
    public boolean isAvailable() {
        // Is the "fallback", if no hooks use it
        return true;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public KothTeam getTeamFrom(UUID playerId) {
        return internalTracker.getTeamFrom(playerId);
    }

    @Override
    public Set<UUID> getTeamMembers(UUID anyTeamMember) {
        return internalTracker.getTeamMembers(anyTeamMember);
    }

    @Override
    public Collection<KothTeam> getAllTeams() {
        return internalTracker.getAllTeams();
    }

    @Override
    public Optional<KothTeam> getTeamByLeader(UUID leader) {
        return internalTracker.getTeamByLeader(leader);
    }

    @Override
    public boolean isTeamMember(UUID uuid) {
        return internalTracker.isTeamMember(uuid);
    }

    @Override
    public boolean isTeamLeader(UUID uuid) {
        return internalTracker.isTeamLeader(uuid);
    }

    @Override
    public boolean areTeammates(UUID player1, UUID player2) {
        return internalTracker.areTeammates(player1, player2);
    }
}