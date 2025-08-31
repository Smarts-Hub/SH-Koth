package dev.smartshub.shkoth.hook.team;

import dev.smartshub.shkoth.api.team.hook.TeamHook;
import dev.smartshub.shkoth.service.team.TeamHookHelpService;
import me.ulrich.clans.api.ClanAPIManager;
import me.ulrich.clans.api.PlayerAPIManager;
import me.ulrich.clans.data.ClanData;
import me.ulrich.clans.interfaces.UClans;
import org.bukkit.Bukkit;

import java.util.*;

public class UClansHook implements TeamHook {

    private ClanAPIManager clanAPI;
    private PlayerAPIManager playerAPI;
    private boolean isAvailable = false;
    private int priority = 0;

    public UClansHook(TeamHookHelpService teamHookHelpService) {
        if (!Bukkit.getPluginManager().isPluginEnabled("UltimateClans")) return;
        isAvailable = teamHookHelpService.isEnabled("ultimate-clans");
        priority = teamHookHelpService.getPriority("ultimate-clans");
    }

    @Override
    public String getPluginName() {
        return "UltimateClans";
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
        Set<UUID> members = new HashSet<>();
        playerAPI.getPlayerClan(anyTeamMember).ifPresent(clanData -> {
            members.addAll(clanData.getMembers());
        });
        return members;
    }

    @Override
    public UUID getTeamLeader(UUID anyTeamMember) {
        Optional<ClanData> clanOpt = playerAPI.getPlayerClan(anyTeamMember);
        if (clanOpt.isEmpty()) return null;
        ClanData clan = clanOpt.get();
        return clan.getLeader();
    }

    @Override
    public String getTeamDisplayName(UUID anyTeamMember) {
        Optional<ClanData> clanOpt = playerAPI.getPlayerClan(anyTeamMember);
        if(clanOpt.isPresent()) {
            return clanOpt.get().getTag();
        }
        return "";
    }

    @Override
    public boolean isTeamMember(UUID uuid) {
        Optional<ClanData> clanOpt = playerAPI.getPlayerClan(uuid);
        return clanOpt.isPresent();
    }

    @Override
    public boolean isTeamLeader(UUID uuid) {
        Optional<ClanData> clanOpt = playerAPI.getPlayerClan(uuid);
        return clanOpt.map(clanData -> clanData.getLeader().equals(uuid)).orElse(false);
    }

    @Override
    public boolean areTeammates(UUID player1, UUID player2) {
        Optional<ClanData> clanOpt1 = playerAPI.getPlayerClan(player1);
        Optional<ClanData> clanOpt2 = playerAPI.getPlayerClan(player2);
        if (clanOpt1.isEmpty() || clanOpt2.isEmpty()) return false;
        return clanOpt1.get().getId().equals(clanOpt2.get().getId());
    }

    @Override
    public boolean validateTeamMembership(UUID playerId) {
        return playerAPI.getPlayerClan(playerId).isPresent();
    }

    @Override
    public Set<UUID> validateTeamMembers(Set<UUID> teamMembers) {
        Set<UUID> validMembers = new HashSet<>();
        for (UUID memberId : teamMembers) {
            if (validateTeamMembership(memberId)) {
                validMembers.add(memberId);
            }
        }
        return validMembers;
    }

    @Override
    public boolean hasTeamChanged(UUID playerId, Set<UUID> lastKnownMembers) {
        Set<UUID> currentMembers = getTeamMembers(playerId);
        return !currentMembers.equals(lastKnownMembers);
    }
}
