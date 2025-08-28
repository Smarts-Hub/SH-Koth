package dev.smartshub.shkoth.hook.team;

import dev.kitteh.factions.FPlayer;
import dev.kitteh.factions.FPlayers;
import dev.smartshub.shkoth.api.team.hook.TeamHook;
import dev.smartshub.shkoth.service.team.TeamHookHelpService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class FactionsUUIDHook implements TeamHook {

    private boolean isAvailable = false;
    private int priority = 0;

    public FactionsUUIDHook(TeamHookHelpService teamHookHelpService) {
        if (!Bukkit.getPluginManager().isPluginEnabled("FactionsUUID")) return;
        isAvailable = teamHookHelpService.isEnabled("factions-uuid");
        priority = teamHookHelpService.getPriority("factions-uuid");
    }
    @Override
    public String getPluginName() {
        return "FactionsUUID";
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
        FPlayer fPlayer = FPlayers.fPlayers().get(anyTeamMember);

        if(!fPlayer.hasFaction()) return members;

        return fPlayer.faction().members().stream().map(FPlayer::asPlayer).map(Player::getUniqueId).
                collect(java.util.stream.Collectors.toSet());
    }

    @Override
    public UUID getTeamLeader(UUID anyTeamMember) {
        FPlayer fPlayer = FPlayers.fPlayers().get(anyTeamMember);

        if(!fPlayer.hasFaction()) return null;
        return fPlayer.faction().admin().uniqueId();
    }

    @Override
    public String getTeamDisplayName(UUID anyTeamMember) {
        FPlayer fPlayer = FPlayers.fPlayers().get(anyTeamMember);

        if(!fPlayer.hasFaction()) return "";
        return fPlayer.faction().tag();
    }

    @Override
    public boolean isTeamMember(UUID uuid) {
        return FPlayers.fPlayers().get(uuid).hasFaction();
    }

    @Override
    public boolean isTeamLeader(UUID uuid) {
        FPlayer fPlayer = FPlayers.fPlayers().get(uuid);
        if(!fPlayer.hasFaction()) return false;
        return fPlayer.faction().admin().uniqueId().equals(uuid);
    }

    @Override
    public boolean areTeammates(UUID player1, UUID player2) {
        FPlayer fPlayer1 = FPlayers.fPlayers().get(player1);
        FPlayer fPlayer2 = FPlayers.fPlayers().get(player2);
        if(!fPlayer1.hasFaction() || !fPlayer2.hasFaction()) return false;
        return fPlayer1.faction().equals(fPlayer2.faction());
    }
}
