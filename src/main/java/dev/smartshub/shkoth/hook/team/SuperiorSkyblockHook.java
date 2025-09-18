package dev.smartshub.shkoth.hook.team;

import dev.smartshub.shkoth.api.team.hook.TeamHook;
import dev.smartshub.shkoth.service.team.TeamHookHelpService;
import org.bukkit.Bukkit;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class SuperiorSkyblockHook implements TeamHook {

    private boolean isAvailable = false;
    private int priority = 0;

    public SuperiorSkyblockHook(TeamHookHelpService teamHookHelpService) {
        if (!Bukkit.getPluginManager().isPluginEnabled("SuperiorSkyblock2")) return;

        isAvailable = teamHookHelpService.isEnabled("superior-skyblock-2");
        priority = teamHookHelpService.getPriority("superior-skyblock-2");
    }

    @Override
    public String getPluginName() {
        return "SuperiorSkyblock2";
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
        SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(anyTeamMember);
        if (superiorPlayer == null) return Set.of();

        Island island = superiorPlayer.getIsland();
        if (island == null) return Set.of();

        return island.getIslandMembers(true).stream()
                .map(SuperiorPlayer::getUniqueId)
                .collect(Collectors.toSet());
    }

    @Override
    public UUID getTeamLeader(UUID anyTeamMember) {
        SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(anyTeamMember);
        if (superiorPlayer == null) return null;

        Island island = superiorPlayer.getIsland();
        if (island == null) return null;

        SuperiorPlayer owner = island.getOwner();
        return owner != null ? owner.getUniqueId() : null;
    }

    @Override
    public String getTeamDisplayName(UUID anyTeamMember) {
        SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(anyTeamMember);
        if (superiorPlayer == null) return "";

        Island island = superiorPlayer.getIsland();
        if (island == null) return "";

        String name = island.getName();
        return name != null ? name : "";
    }

    @Override
    public boolean isTeamMember(UUID uuid) {
        SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(uuid);
        if (superiorPlayer == null) return false;

        return superiorPlayer.getIsland() != null;
    }

    @Override
    public boolean isTeamLeader(UUID uuid) {
        SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(uuid);
        if (superiorPlayer == null) return false;

        Island island = superiorPlayer.getIsland();
        if (island == null) return false;

        SuperiorPlayer owner = island.getOwner();
        return owner != null && owner.getUniqueId().equals(uuid);
    }

    @Override
    public boolean areTeammates(UUID player1, UUID player2) {
        SuperiorPlayer superiorPlayer1 = SuperiorSkyblockAPI.getPlayer(player1);
        SuperiorPlayer superiorPlayer2 = SuperiorSkyblockAPI.getPlayer(player2);

        if (superiorPlayer1 == null || superiorPlayer2 == null) return false;

        Island island1 = superiorPlayer1.getIsland();
        Island island2 = superiorPlayer2.getIsland();

        if (island1 == null || island2 == null) return false;

        return island1.equals(island2);
    }

    @Override
    public boolean validateTeamMembership(UUID playerId) {
        return isTeamMember(playerId);
    }

    @Override
    public Set<UUID> validateTeamMembers(Set<UUID> teamMembers) {
        return teamMembers.stream()
                .filter(this::isTeamMember)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean hasTeamChanged(UUID playerId, Set<UUID> lastKnownMembers) {
        Set<UUID> currentMembers = getTeamMembers(playerId);
        return !currentMembers.equals(lastKnownMembers);
    }
}