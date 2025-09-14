package dev.smartshub.shkoth.hook.team;

import dev.smartshub.shkoth.api.team.hook.TeamHook;
import dev.smartshub.shkoth.service.team.TeamHookHelpService;
import org.bukkit.Bukkit;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.managers.IslandsManager;
import world.bentobox.bentobox.managers.PlayersManager;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class BentoBoxHook implements TeamHook {

    private boolean isAvailable = false;
    private int priority = 0;
    private BentoBox bentoBox;
    private IslandsManager islandsManager;
    private PlayersManager playersManager;

    public BentoBoxHook(TeamHookHelpService teamHookHelpService) {
        if (!Bukkit.getPluginManager().isPluginEnabled("BentoBox")) return;

        this.bentoBox = BentoBox.getInstance();
        if (this.bentoBox == null) return;

        this.islandsManager = bentoBox.getIslands();
        this.playersManager = bentoBox.getPlayers();

        isAvailable = teamHookHelpService.isEnabled("bentobox");
        priority = teamHookHelpService.getPriority("bentobox");
    }

    @Override
    public String getPluginName() {
        return "BentoBox";
    }

    @Override
    public boolean isAvailable() {
        return isAvailable && bentoBox != null;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public Set<UUID> getTeamMembers(UUID anyTeamMember) {
        if (!isAvailable()) return Set.of();

        Optional<Island> island = getPlayerIsland(anyTeamMember);
        if (!island.isPresent()) return Set.of();

        Set<UUID> allMembers = island.get().getMembers().keySet();
        return allMembers.stream()
                .filter(uuid -> uuid != null)
                .collect(Collectors.toSet());
    }

    @Override
    public UUID getTeamLeader(UUID anyTeamMember) {
        if (!isAvailable()) return null;

        Optional<Island> island = getPlayerIsland(anyTeamMember);
        if (!island.isPresent()) return null;

        return island.get().getOwner();
    }

    @Override
    public String getTeamDisplayName(UUID anyTeamMember) {
        if (!isAvailable()) return "";

        Optional<Island> island = getPlayerIsland(anyTeamMember);
        if (!island.isPresent()) return "";

        String name = island.get().getName();
        if (name == null || name.isEmpty()) {
            UUID owner = island.get().getOwner();
            if (owner != null) {
                name = playersManager.getName(owner) + "'s Island";
            } else {
                name = "Island #" + island.get().getUniqueId().toString().substring(0, 8);
            }
        }
        return name;
    }

    @Override
    public boolean isTeamMember(UUID uuid) {
        return isPlayerInAnyTeam(uuid);
    }

    @Override
    public boolean isTeamLeader(UUID uuid) {
        if (!isAvailable()) return false;

        Optional<Island> island = getPlayerIsland(uuid);
        if (!island.isPresent()) return false;

        return uuid.equals(island.get().getOwner());
    }

    @Override
    public boolean areTeammates(UUID player1, UUID player2) {
        if (!isAvailable()) return false;

        Optional<Island> island1 = getPlayerIsland(player1);
        Optional<Island> island2 = getPlayerIsland(player2);

        if (!island1.isPresent() || !island2.isPresent()) return false;

        return island1.get().getUniqueId().equals(island2.get().getUniqueId());
    }

    @Override
    public boolean validateTeamMembership(UUID playerId) {
        return isPlayerInAnyTeam(playerId);
    }

    @Override
    public Set<UUID> validateTeamMembers(Set<UUID> teamMembers) {
        return teamMembers.stream()
                .filter(this::isPlayerInAnyTeam)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean hasTeamChanged(UUID playerId, Set<UUID> lastKnownMembers) {
        Set<UUID> currentMembers = getTeamMembers(playerId);
        return !currentMembers.equals(lastKnownMembers);
    }

    private Optional<Island> getPlayerIsland(UUID playerId) {
        if (!isAvailable() || playerId == null) return Optional.empty();

        return bentoBox.getIWM().getOverWorlds().stream()
                .map(world -> islandsManager.getIsland(world, playerId))
                .filter(island -> island != null)
                .findFirst();
    }

    private boolean isPlayerInAnyTeam(UUID playerId) {
        if (!isAvailable() || playerId == null) return false;

        return bentoBox.getIWM().getOverWorlds().stream()
                .anyMatch(world -> islandsManager.hasIsland(world, playerId) || islandsManager.inTeam(world, playerId));
    }
}