package dev.smartshub.shkoth.hook.team;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import dev.smartshub.shkoth.api.team.hook.TeamHook;
import dev.smartshub.shkoth.service.team.TeamHookHelpService;
import org.bukkit.Bukkit;

import java.util.Set;
import java.util.UUID;

public class TownyHook implements TeamHook {

    private boolean isAvailable = false;
    private int priority = 0;

    public TownyHook(TeamHookHelpService teamHookHelpService) {
        if (!Bukkit.getPluginManager().isPluginEnabled("Towny")) return;
        isAvailable = teamHookHelpService.isEnabled("towny");
        priority = teamHookHelpService.getPriority("towny");
    }
    @Override
    public String getPluginName() {
        return "Towny";
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
        Town town = TownyAPI.getInstance().getTown(anyTeamMember);
        if(town == null) return members;

        return town.getResidents().stream().map(Resident::getUUID).collect(java.util.stream.Collectors.toSet());
    }

    @Override
    public UUID getTeamLeader(UUID anyTeamMember) {
        Town town = TownyAPI.getInstance().getTown(anyTeamMember);
        if(town == null) return null;

        return town.getMayor().getUUID();
    }

    @Override
    public String getTeamDisplayName(UUID anyTeamMember) {
        Town town = TownyAPI.getInstance().getTown(anyTeamMember);
        if(town == null) return "";

        return town.getName();
    }

    @Override
    public boolean isTeamMember(UUID uuid) {
        Town town = TownyAPI.getInstance().getTown(uuid);
        return town != null;
    }

    @Override
    public boolean isTeamLeader(UUID uuid) {
        Town town = TownyAPI.getInstance().getTown(uuid);
        if(town == null) return false;

        return town.getMayor().getUUID().equals(uuid);
    }

    @Override
    public boolean areTeammates(UUID player1, UUID player2) {
        Town town1 = TownyAPI.getInstance().getTown(player1);
        Town town2 = TownyAPI.getInstance().getTown(player2);
        if(town1 == null || town2 == null) return false;

        return town1.equals(town2);
    }
}
