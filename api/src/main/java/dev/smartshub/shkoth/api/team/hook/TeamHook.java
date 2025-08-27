package dev.smartshub.shkoth.api.team.hook;

import dev.smartshub.shkoth.api.team.KothTeam;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface TeamHook {
    String getPluginName();
    boolean isAvailable();
    int getPriority();
    
    KothTeam getTeamFrom(UUID playerId);
    Set<UUID> getTeamMembers(UUID anyTeamMember);
    Collection<KothTeam> getAllTeams();
    Optional<KothTeam> getTeamByLeader(UUID leader);
    
    boolean isTeamMember(UUID uuid);
    boolean isTeamLeader(UUID uuid);
    boolean areTeammates(UUID player1, UUID player2);
}