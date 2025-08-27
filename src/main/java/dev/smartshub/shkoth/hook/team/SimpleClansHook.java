package dev.smartshub.shkoth.hook.team;

import dev.smartshub.shkoth.api.team.hook.TeamHook;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.Bukkit;

import java.util.Set;
import java.util.UUID;

public class SimpleClansHook implements TeamHook {

    private SimpleClans simpleClansAPI;
    private boolean isAvailable = false;

    public SimpleClansHook() {
        if (!Bukkit.getPluginManager().isPluginEnabled("UltimateClans")) return;
        simpleClansAPI = (SimpleClans) Bukkit.getPluginManager().getPlugin("UltimateClans");
        isAvailable = true;
    }

    @Override
    public String getPluginName() {
        return "SimpleClans";
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
        var clan = getClan(anyTeamMember);
        if(clan == null) return members;

        return clan.getMembers().stream()
                .map(ClanPlayer::getUniqueId)
                .collect(java.util.stream.Collectors.toSet());
    }

    @Override
    public UUID getTeamLeader(UUID anyTeamMember) {
        var clan = getClan(anyTeamMember);
        if(clan == null) return null;

        return clan.getLeaders().getFirst().getUniqueId();
    }

    @Override
    public String getTeamDisplayName(UUID anyTeamMember) {
        var clan = getClan(anyTeamMember);
        if(clan == null) return null;
        return clan.getName();
    }

    @Override
    public boolean isTeamMember(UUID uuid) {
        var clanPlayer = simpleClansAPI.getClanManager().getClanPlayer(uuid);
        return clanPlayer != null && clanPlayer.getClan() != null;
    }

    @Override
    public boolean isTeamLeader(UUID uuid) {
        var clan = getClan(uuid);
        if(clan == null) return false;

        return clan.getLeaders().stream()
                .anyMatch(leader -> leader.getUniqueId().equals(uuid));
    }

    @Override
    public boolean areTeammates(UUID player1, UUID player2) {
        var clan1 = getClan(player1);
        var clan2 = getClan(player2);
        if(clan1 == null || clan2 == null) return false;

        return clan1.getName().equals(clan2.getName());
    }

    public Clan getClan(UUID anyTeamMember) {
        var clanPlayer = simpleClansAPI.getClanManager().getClanPlayer(anyTeamMember);
        if(clanPlayer == null) return null;
        return clanPlayer.getClan();
    }
}