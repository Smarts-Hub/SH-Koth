package dev.smartshub.shkoth.hook.team;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import dev.smartshub.shkoth.api.team.hook.TeamHook;
import dev.smartshub.shkoth.service.team.TeamHookHelpService;
import org.bukkit.Bukkit;

import java.util.Comparator;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class BetterTeamsHook implements TeamHook {

    private boolean isAvailable = false;
    private int priority = 0;

    public BetterTeamsHook(TeamHookHelpService teamHookHelpService) {
        if (!Bukkit.getPluginManager().isPluginEnabled("BetterTeams")) return;
        isAvailable = teamHookHelpService.isEnabled("better-teams");
        priority = teamHookHelpService.getPriority("better-teams");
    }

    @Override
    public String getPluginName() {
        return "BetterTeams";
    }

    @Override
    public boolean isAvailable() {
        return isAvailable;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public Set<UUID> getTeamMembers(UUID anyTeamMember) {
        Set<UUID> members = Set.of();
        Team team = Team.getTeam(anyTeamMember);
        if(team == null) return members;

        return team.getMembers().get().stream().map(TeamPlayer::getPlayerUUID).collect(Collectors.toSet());
    }

    @Override
    public UUID getTeamLeader(UUID anyTeamMember) {
        Team team = Team.getTeam(anyTeamMember);
        if (team == null) return null;

        return team.getMembers().get().stream()
                .max(Comparator.comparingInt(member -> member.getRank().value))
                .map(TeamPlayer::getPlayerUUID)
                .orElse(null);
    }


    @Override
    public String getTeamDisplayName(UUID anyTeamMember) {
        Team team = Team.getTeam(anyTeamMember);
        if(team == null) return null;

        return team.getDisplayName();
    }

    @Override
    public boolean isTeamMember(UUID uuid) {
        return Team.getTeam(uuid) != null;
    }

    @Override
    public boolean isTeamLeader(UUID uuid) {
        Team team = Team.getTeam(uuid);
        if (team == null) return false;

        return team.getMembers().get().stream()
                .max(Comparator.comparingInt(member -> member.getRank().value))
                .map(topMember -> topMember.getPlayerUUID().equals(uuid))
                .orElse(false);
    }


    @Override
    public boolean areTeammates(UUID player1, UUID player2) {
        Team team1 = Team.getTeam(player1);
        Team team2 = Team.getTeam(player2);
        if(team1 == null || team2 == null) return false;

        return team1.equals(team2);
    }

    @Override
    public boolean validateTeamMembership(UUID playerId) {
        return isTeamMember(playerId);
    }

    @Override
    public Set<UUID> validateTeamMembers(Set<UUID> teamMembers) {
        return teamMembers.stream().filter(this::isTeamMember).collect(Collectors.toSet());
    }

    @Override
    public boolean hasTeamChanged(UUID playerId, Set<UUID> lastKnownMembers) {
        Set<UUID> currentMembers = getTeamMembers(playerId);
        return !currentMembers.equals(lastKnownMembers);
    }
}
