package dev.smartshub.shkoth.hook.team;

import dev.smartshub.shkoth.api.team.hook.TeamHook;
import org.bukkit.Bukkit;
import org.kingdoms.constants.group.Kingdom;

import java.util.Set;
import java.util.UUID;

public class KingdomsXHook implements TeamHook {

    private boolean isAvailable = false;

    public KingdomsXHook(){
        if(!Bukkit.getPluginManager().isPluginEnabled("KingdomsX")) return;
        isAvailable = true;
    }

    @Override
    public String getPluginName() {
        return "KingdomsX";
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
        Kingdom kingdom = Kingdom.getKingdom(anyTeamMember);
        if(kingdom == null) return members;

        return kingdom.getMembers();
    }

    @Override
    public UUID getTeamLeader(UUID anyTeamMember) {
        Kingdom kingdom = Kingdom.getKingdom(anyTeamMember);
        if(kingdom == null) return null;

        return kingdom.getOwnerId();
    }

    @Override
    public String getTeamDisplayName(UUID anyTeamMember) {
        Kingdom kingdom = Kingdom.getKingdom(anyTeamMember);
        if(kingdom == null) return "";

        return kingdom.getName();
    }

    @Override
    public boolean isTeamMember(UUID uuid) {
        return Kingdom.getKingdom(uuid) != null;
    }

    @Override
    public boolean isTeamLeader(UUID uuid) {
        Kingdom kingdom = Kingdom.getKingdom(uuid);
        if(kingdom == null) return false;

        return kingdom.getOwnerId().equals(uuid);
    }

    @Override
    public boolean areTeammates(UUID player1, UUID player2) {
        Kingdom kingdom1 = Kingdom.getKingdom(player1);
        Kingdom kingdom2 = Kingdom.getKingdom(player2);
        if(kingdom1 == null || kingdom2 == null) return false;

        return kingdom1.equals(kingdom2);
    }
}
