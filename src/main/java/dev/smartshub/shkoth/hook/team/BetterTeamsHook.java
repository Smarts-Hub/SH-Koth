package dev.smartshub.shkoth.hook.team;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import dev.smartshub.shkoth.api.team.hook.TeamHook;
import org.bukkit.Bukkit;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class BetterTeamsHook implements TeamHook {

    private boolean isAvailable = false;

    public BetterTeamsHook(){
        if (!Bukkit.getPluginManager().isPluginEnabled("BetterTeams")) return;
        isAvailable = true;
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
        return 10;
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
        if(team == null) return null;

        // BetterTeams does not have a specific leader concept, so we return the first member
        return team.getMembers().getOnlinePlayers().getFirst().getUniqueId();
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
        if(team == null) return false;

        return team.getMembers().get().stream().anyMatch(member -> member.getPlayerUUID().equals(uuid));
    }

    @Override
    public boolean areTeammates(UUID player1, UUID player2) {
        Team team1 = Team.getTeam(player1);
        Team team2 = Team.getTeam(player2);
        if(team1 == null || team2 == null) return false;

        return team1.equals(team2);
    }
}
