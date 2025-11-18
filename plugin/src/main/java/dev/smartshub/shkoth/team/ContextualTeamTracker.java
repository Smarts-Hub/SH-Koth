package dev.smartshub.shkoth.team;

import dev.smartshub.shkoth.api.team.KothTeam;
import dev.smartshub.shkoth.api.team.TeamWrapper;
import dev.smartshub.shkoth.api.team.handle.TeamHandler;
import dev.smartshub.shkoth.api.team.hook.TeamHook;
import dev.smartshub.shkoth.api.team.track.TeamTracker;
import dev.smartshub.shkoth.hook.team.*;
import dev.smartshub.shkoth.service.team.TeamHookHelpService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class ContextualTeamTracker implements TeamTracker {

    private final List<TeamHook> hooks = new ArrayList<>();
    private final TeamHandler internalHandler;
    private final TeamHookHelpService teamHookHelpService;

    public ContextualTeamTracker(TeamHookHelpService teamHookHelpService) {
        this.teamHookHelpService = teamHookHelpService;
        this.internalHandler = new InternalTeamHandler();
        setupHooks();
    }

    private void setupHooks() {
        registerHook(new SimpleClansHook(teamHookHelpService));
        registerHook(new FactionsUUIDHook(teamHookHelpService));
        registerHook(new BetterTeamsHook(teamHookHelpService));
        registerHook(new TownyHook(teamHookHelpService));
        registerHook(new KingdomsXHook(teamHookHelpService));
        registerHook(new UClansHook(teamHookHelpService));
        registerHook(new SuperiorSkyblockHook(teamHookHelpService));
    }

    public void registerHook(TeamHook hook) {
        hooks.removeIf(h -> h.getPluginName().equalsIgnoreCase(hook.getPluginName()));
        hooks.add(hook);
        hooks.sort(Comparator.comparingInt(TeamHook::getPriority).reversed());
    }

    private TeamHook getActiveHook() {
        return hooks.stream()
                .filter(TeamHook::isAvailable)
                .findFirst()
                .orElse(null);
    }

    public TeamWrapper getTeamForKoth(UUID playerId, boolean isSoloMode) {
        if (isSoloMode) {
            return createSoloTeamWrapper(playerId);
        } else {
            return getTeamWrapper(playerId);
        }
    }

    private TeamWrapper createSoloTeamWrapper(UUID playerId) {
        Player player = Bukkit.getPlayer(playerId);
        String displayName = player != null ? player.getName() : "Unknown";

        return new TeamWrapper(
                playerId,
                Set.of(playerId),
                displayName,
                true);
    }

    public TeamWrapper getTeamWrapper(UUID playerId) {
        TeamHook activeHook = getActiveHook();

        // If no external team plugin is active, use the internal handler.
        if (activeHook == null) {
            KothTeam internalTeam = internalHandler.getTeam(playerId);
            // If the player has an internal team, wrap it and return.
            return internalTeam != null ? new TeamWrapper(internalTeam, false) : null;
        }

        // --- Live Data Fetch from the Active Hook ---
        // Always get the most current list of team members. This is the key fix.
        Set<UUID> members = activeHook.getTeamMembers(playerId);

        // If the hook returns no members, the player is not in a team.
        if (members == null || members.isEmpty()) {
            return null;
        }

        // Get the current display name and create a new wrapper with this fresh data.
        String displayName = activeHook.getTeamDisplayName(playerId);
        return new TeamWrapper(playerId, members, displayName, false);
    }

    public TeamWrapper createInternalTeam(UUID leaderId, int maxMembers) {
        if (getActiveHook() != null) {
            return null;
        }

        KothTeam internalTeam = internalHandler.createTeam(leaderId, maxMembers);
        return internalTeam != null ? new TeamWrapper(internalTeam, false) : null;
    }

    @Override
    public Set<UUID> getTeamMembers(UUID uuid) {
        TeamHook activeHook = getActiveHook();
        if (activeHook != null) {
            return activeHook.getTeamMembers(uuid);
        }
        return internalHandler.getTeamMembers(uuid);
    }

    @Override
    public boolean isTeamMember(UUID uuid) {
        TeamHook activeHook = getActiveHook();
        if (activeHook != null) {
            return activeHook.isTeamMember(uuid);
        }
        return internalHandler.isTeamMember(uuid);
    }

    @Override
    public boolean isTeamLeader(UUID uuid) {
        TeamHook activeHook = getActiveHook();
        if (activeHook != null) {
            return activeHook.isTeamLeader(uuid);
        }
        return internalHandler.isTeamLeader(uuid);
    }

    @Override
    public boolean areTeammates(UUID player1, UUID player2) {
        TeamHook activeHook = getActiveHook();
        if (activeHook != null) {
            return activeHook.areTeammates(player1, player2);
        }
        return internalHandler.areTeammates(player1, player2);
    }

    @Override
    public String getTeamDisplayName(UUID anyTeamMember) {
        TeamHook activeHook = getActiveHook();
        if (activeHook != null) {
            return activeHook.getTeamDisplayName(anyTeamMember);
        }
        return internalHandler.getTeamDisplayName(anyTeamMember);
    }

    @Override
    public String getActiveProvider() {
        TeamHook activeHook = getActiveHook();
        return activeHook != null ? activeHook.getPluginName() : "Internal";
    }

    public TeamHandler getInternalHandler() {
        return internalHandler;
    }

    public void updateTeams() {
        // The only responsibility is to update the internal teams, if any are used.
        internalHandler.updateTeams();
    }
}