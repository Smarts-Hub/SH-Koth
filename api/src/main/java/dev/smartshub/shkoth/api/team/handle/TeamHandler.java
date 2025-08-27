package dev.smartshub.shkoth.api.team.handle;

import dev.smartshub.shkoth.api.team.KothTeam;

import java.util.UUID;

public interface TeamHandler {
    KothTeam createTeam(UUID leader);
    boolean addMemberToTeam(UUID member, UUID teamLeader);
    boolean removeMemberFromTeam(UUID member);
    boolean disbandTeam(UUID leader);
    boolean transferLeadership(UUID oldLeader, UUID newLeader);

}
