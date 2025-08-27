package dev.smartshub.shkoth.api.team.handle;

import dev.smartshub.shkoth.api.team.KothTeam;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface TeamHandler {
    KothTeam createTeam(UUID leader, int maxMembers);
    KothTeam createTeam(UUID leader);
    boolean addMemberToTeam(UUID member, UUID teamLeader);
    boolean removeMemberFromTeam(UUID member);
    boolean disbandTeam(UUID leader);
    boolean transferLeadership(UUID oldLeader, UUID newLeader);

    KothTeam getTeam(UUID playerId);
    Set<UUID> getTeamMembers(UUID playerId);
    boolean isTeamMember(UUID uuid);
    boolean isTeamLeader(UUID uuid);
    boolean areTeammates(UUID player1, UUID player2);
    String getTeamDisplayName(UUID playerId);
    Collection<? extends KothTeam> getAllTeams();
}
