package dev.smartshub.shkoth.api.team.track;

import dev.smartshub.shkoth.api.team.KothTeam;

import java.util.Set;
import java.util.UUID;

public interface TeamTracker {
    Set<UUID> getTeamMembers(UUID uuid);
    boolean isTeamMember(UUID uuid);
    boolean isTeamLeader(UUID uuid);
    boolean areTeammates(UUID player1, UUID player2);

    String getTeamDisplayName(UUID uuid);
    String getActiveProvider();
}

