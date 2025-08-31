package dev.smartshub.shkoth.api.team.hook;

import java.util.Set;
import java.util.UUID;

public interface TeamHook {
    String getPluginName();
    boolean isAvailable();
    int getPriority();

    Set<UUID> getTeamMembers(UUID anyTeamMember);
    UUID getTeamLeader(UUID anyTeamMember);

    String getTeamDisplayName(UUID anyTeamMember);

    boolean isTeamMember(UUID uuid);
    boolean isTeamLeader(UUID uuid);
    boolean areTeammates(UUID player1, UUID player2);
    boolean validateTeamMembership(UUID playerId);

    Set<UUID> validateTeamMembers(Set<UUID> teamMembers);
    boolean hasTeamChanged(UUID playerId, Set<UUID> lastKnownMembers);
}