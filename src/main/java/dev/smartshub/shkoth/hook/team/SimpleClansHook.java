package dev.smartshub.shkoth.hook.team;

import dev.smartshub.shkoth.api.team.KothTeam;
import dev.smartshub.shkoth.api.team.hook.TeamHook;
import org.bukkit.Bukkit;

import java.util.*;

public class SimpleClansHook implements TeamHook {

    @Override
    public String getPluginName() {
        return "SimpleClans";
    }

    @Override
    public boolean isAvailable() {
        return Bukkit.getPluginManager().getPlugin("SimpleClans") != null;
    }

    @Override
    public int getPriority() {
        return 10; //Make it configurable later
    }

    @Override
    public KothTeam getTeamFrom(UUID playerId) {
        if (!isAvailable()) return null;
        //TODO
        return null;
    }

    @Override
    public Set<UUID> getTeamMembers(UUID anyTeamMember) {
        KothTeam team = getTeamFrom(anyTeamMember);
        return team != null ? team.getMembers() : Set.of();
    }

    @Override
    public Collection<KothTeam> getAllTeams() {
        if (!isAvailable()) return Collections.emptyList();
        //TODO
        return null;
    }

    @Override
    public Optional<KothTeam> getTeamByLeader(UUID leader) {
        KothTeam team = getTeamFrom(leader);
        return team != null && team.isLeader(leader) ? Optional.of(team) : Optional.empty();
    }

    @Override
    public boolean isTeamMember(UUID uuid) {
        return getTeamFrom(uuid) != null;
    }

    @Override
    public boolean isTeamLeader(UUID uuid) {
        KothTeam team = getTeamFrom(uuid);
        return team != null && team.isLeader(uuid);
    }

    @Override
    public boolean areTeammates(UUID player1, UUID player2) {
        if (player1.equals(player2)) return true;
        KothTeam team = getTeamFrom(player1);
        return team != null && team.contains(player2);
    }
}