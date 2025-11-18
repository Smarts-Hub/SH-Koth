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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ConcurrentHashMap;

import java.util.*;

public class ContextualTeamTracker implements TeamTracker {

    private final List<TeamHook> hooks = new ArrayList<>();
    private final TeamHandler internalHandler;
    private final TeamHookHelpService teamHookHelpService;
    private final Map<UUID, TeamWrapper> tickCache = new ConcurrentHashMap<>();

    public ContextualTeamTracker(TeamHookHelpService teamHookHelpService, JavaPlugin plugin) {
        this.teamHookHelpService = teamHookHelpService;
        this.internalHandler = new InternalTeamHandler();
        setupHooks();

        // This task clears the cache at the start of every tick, ensuring data is never
        // stale.
        new BukkitRunnable() {
            @Override
            public void run() {
                tickCache.clear();
            }
        }.runTaskTimer(plugin, 0L, 1L);
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
        // 1. Check the tick-cache first for an instant result.
        TeamWrapper cachedWrapper = tickCache.get(playerId);
        if (cachedWrapper != null) {
            return cachedWrapper;
        }

        TeamHook activeHook = getActiveHook();
        TeamWrapper freshWrapper;

        // 2. If not cached, fetch the data from the appropriate source (hook or
        // internal).
        if (activeHook == null) {
            KothTeam internalTeam = internalHandler.getTeam(playerId);
            freshWrapper = internalTeam != null ? new TeamWrapper(internalTeam, false) : null;
        } else {
            Set<UUID> members = activeHook.getTeamMembers(playerId);
            if (members == null || members.isEmpty()) {
                freshWrapper = null;
            } else {
                String displayName = activeHook.getTeamDisplayName(playerId);
                freshWrapper = new TeamWrapper(playerId, members, displayName, false);
            }
        }

        // 3. Before returning, populate the cache for all members of the team.
        // This prevents redundant lookups for teammates within the same tick.
        if (freshWrapper != null) {
            for (UUID memberId : freshWrapper.getMembers()) {
                tickCache.put(memberId, freshWrapper);
            }
        }

        return freshWrapper;
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