package dev.smartshub.shkoth.api.team.track;

import dev.smartshub.shkoth.api.team.KothTeam;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface TeamTracker {
    KothTeam getTeamFrom(UUID uuid);
    Set<UUID> getTeamMembers(UUID anyTeamMember);
    Collection<KothTeam> getAllTeams();
    Optional<KothTeam> getTeamByLeader(UUID leader);

    boolean isTeamMember(UUID uuid);
    boolean isTeamLeader(UUID uuid);
    boolean areTeammates(UUID player1, UUID player2);

    KothTeam createTeam(UUID leader);
    boolean canCreateTeams();
    boolean canManageTeams();

    boolean addMemberToTeam(UUID member, UUID teamLeader);
    boolean removeMemberFromTeam(UUID member);
    boolean disbandTeam(UUID leader);
    boolean transferLeadership(UUID oldLeader, UUID newLeader);

    String getTeamDisplayName(KothTeam team);
    String getActiveProvider();
}
