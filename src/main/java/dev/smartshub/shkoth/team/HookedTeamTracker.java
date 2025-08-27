package dev.smartshub.shkoth.team;

import dev.smartshub.shkoth.api.team.hook.TeamHook;
import dev.smartshub.shkoth.api.team.track.TeamTracker;

import java.util.*;

public class HookedTeamTracker implements TeamTracker {

    private static HookedTeamTracker instance;

    private final List<TeamHook> hooks = new ArrayList<>();
    private final InternalTeamHook internalHook;

    private HookedTeamTracker() {
        this.internalHook = new InternalTeamHook();
        setupHooks();
    }

    public static HookedTeamTracker getInstance() {
        if (instance == null) {
            instance = new HookedTeamTracker();
        }
        return instance;
    }

    private void setupHooks() {
        //TODO: register external hooks here
        registerHook(internalHook);
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

    private boolean isUsingInternalSystem() {
        return getActiveHook() instanceof InternalTeamHook;
    }

    @Override
    public Set<UUID> getTeamMembers(UUID uuid) {
        TeamHook activeHook = getActiveHook();
        return activeHook != null ? activeHook.getTeamMembers(uuid) : Set.of();
    }

    @Override
    public boolean isTeamMember(UUID uuid) {
        TeamHook activeHook = getActiveHook();
        return activeHook != null && activeHook.isTeamMember(uuid);
    }

    @Override
    public boolean isTeamLeader(UUID uuid) {
        TeamHook activeHook = getActiveHook();
        return activeHook != null && activeHook.isTeamLeader(uuid);
    }

    @Override
    public boolean areTeammates(UUID player1, UUID player2) {
        TeamHook activeHook = getActiveHook();
        return activeHook != null && activeHook.areTeammates(player1, player2);
    }

    @Override
    public String getTeamDisplayName(UUID anyTeamMember) {
        TeamHook activeHook = getActiveHook();
        return activeHook != null ? activeHook.getTeamDisplayName(anyTeamMember) : "No Team";
    }

    @Override
    public String getActiveProvider() {
        TeamHook activeHook = getActiveHook();
        return activeHook != null ? activeHook.getPluginName() : "None";
    }

    public InternalTeamHook getInternalHook() {
        return internalHook;
    }
}
