package dev.smartshub.shkoth.api.team;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface TeamTracker {

    Team getTeamFrom(UUID uuid);
    Team createTeam(UUID leader);

    Set<UUID> getTeamMembers(UUID anyTeamMember);
    Collection<Team> getAllTeams();
    Optional<Team> getTeamByLeader(UUID leader);

    boolean isTeamMember(UUID uuid);
    boolean isTeamLeader(UUID uuid);
    boolean areTeammates(UUID player1, UUID player2);
    void dissolveTeam(UUID leader);

    void addMember(UUID uuid, Team team);
    void removeMember(UUID uuid);
    void clearTeam(UUID uuid);
    void clearAllTeams();

    Team updateLeader(UUID oldLeader, UUID newLeader);

   String getTeamDisplayName(Team team);
}
