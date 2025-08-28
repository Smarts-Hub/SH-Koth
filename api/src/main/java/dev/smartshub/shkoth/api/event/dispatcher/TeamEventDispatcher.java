package dev.smartshub.shkoth.api.event.dispatcher;

import dev.smartshub.shkoth.api.event.team.*;
import dev.smartshub.shkoth.api.team.KothTeam;
import org.bukkit.Bukkit;

import java.util.UUID;

public class TeamEventDispatcher {

    public MemberJoinedTeamEvent fireMemberJoinedTeamEvent(KothTeam oldTeam, KothTeam newTeam, UUID joinedMember, UUID inviter) {
        MemberJoinedTeamEvent event = new MemberJoinedTeamEvent(oldTeam, newTeam, joinedMember, inviter);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public void fireMemberLeavedTeamEvent(KothTeam oldTeam, KothTeam newTeam, UUID removed, MemberLeavedTeamEvent.RemovalReason reason) {
        MemberLeavedTeamEvent event = new MemberLeavedTeamEvent(oldTeam, newTeam, removed, reason);
        Bukkit.getPluginManager().callEvent(event);
    }

    public void fireMemberKickedFromTeamEvent(KothTeam oldTeam, UUID kickedMember, UUID kicker) {
        MemberKickedFromTeamEvent event = new MemberKickedFromTeamEvent(oldTeam, kickedMember, kicker);
        Bukkit.getPluginManager().callEvent(event);
    }

    public TeamChangeLeaderEvent fireTeamChangeLeaderEvent(KothTeam team, UUID oldLeader, UUID newLeader) {
        TeamChangeLeaderEvent event = new TeamChangeLeaderEvent(team, oldLeader, newLeader);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public void fireTeamCreatedEvent(KothTeam team, TeamCreatedEvent.CreationReason reason) {
        TeamCreatedEvent event = new TeamCreatedEvent(team, reason);
        Bukkit.getPluginManager().callEvent(event);
    }

    public void fireTeamDissolvedEvent(KothTeam team, UUID dissolver, TeamDissolvedEvent.DissolutionReason reason) {
        TeamDissolvedEvent event = new TeamDissolvedEvent(team, dissolver, reason);
        Bukkit.getPluginManager().callEvent(event);
    }


}
