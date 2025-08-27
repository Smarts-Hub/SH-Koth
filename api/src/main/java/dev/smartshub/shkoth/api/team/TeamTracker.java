package dev.smartshub.shkoth.api.team;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface TeamTracker<T> {

    T getTeamFrom(UUID uuid);
    T createTeam(UUID leader);

    Set<UUID> getTeamMembers(UUID anyTeamMember);
    Collection<T> getAllTeams();
    Optional<T> getTeamByLeader(UUID leader);

    boolean isTeamMember(UUID uuid);
    boolean isTeamLeader(UUID uuid);
    boolean areTeammates(UUID player1, UUID player2);

   String getTeamDisplayName(Team team);
}
