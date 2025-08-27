package dev.smartshub.shkoth.api.team.track;

import dev.smartshub.shkoth.api.team.KothTeam;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface TeamTracker {

    KothTeam getTeamFrom(UUID uuid);
    KothTeam createTeam(UUID leader);

    Set<UUID> getTeamMembers(UUID anyTeamMember);
    Collection<KothTeam> getAllTeams();
    Optional<KothTeam> getTeamByLeader(UUID leader);

    boolean isTeamMember(UUID uuid);
    boolean isTeamLeader(UUID uuid);
    boolean areTeammates(UUID player1, UUID player2);

    String getTeamDisplayName(KothTeam team);
}
