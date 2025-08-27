package dev.smartshub.shkoth.team.track;

import dev.smartshub.shkoth.api.team.KothTeam;
import dev.smartshub.shkoth.api.team.hook.TeamHook;
import dev.smartshub.shkoth.api.team.track.TeamTracker;
import dev.smartshub.shkoth.hook.team.SimpleClansHook;
import dev.smartshub.shkoth.team.InternalTeamHook;

import java.util.*;

public class HookedTeamTracker implements TeamTracker {
    
    private static HookedTeamTracker instance;
    private final List<TeamHook> hooks = new ArrayList<>();
    private final InternalTeamTracker internalTracker;
    
    private HookedTeamTracker() {
        this.internalTracker = InternalTeamTracker.getInstance();
        setupHooks();
    }
    
    public static HookedTeamTracker getInstance() {
        if (instance == null) {
            instance = new HookedTeamTracker();
        }
        return instance;
    }
    
    private void setupHooks() {
        registerHook(new SimpleClansHook());
        // TODO: others hooks
        
        registerHook(new InternalTeamHook(internalTracker));
    }
    
    public void registerHook(TeamHook hook) {
        hooks.removeIf(h -> h.getPluginName().equals(hook.getPluginName()));
        hooks.add(hook);
        hooks.sort(Comparator.comparingInt(TeamHook::getPriority).reversed());
    }
    
    private TeamHook getActiveHook() {
        return hooks.stream()
                .filter(TeamHook::isAvailable)
                .findFirst()
                .orElse(null);
    }

    @Override
    public KothTeam getTeamFrom(UUID uuid) {
        TeamHook activeHook = getActiveHook();
        return activeHook != null ? activeHook.getTeamFrom(uuid) : null;
    }

    @Override
    public KothTeam createTeam(UUID leader) {
        TeamHook activeHook = getActiveHook();
        if (activeHook instanceof InternalTeamHook) {
            return internalTracker.createTeam(leader);
        }
        throw new UnsupportedOperationException("Cannot create teams when external plugin is active");
    }

    @Override
    public Set<UUID> getTeamMembers(UUID anyTeamMember) {
        TeamHook activeHook = getActiveHook();
        return activeHook != null ? activeHook.getTeamMembers(anyTeamMember) : Set.of();
    }

    @Override
    public Collection<KothTeam> getAllTeams() {
        TeamHook activeHook = getActiveHook();
        return activeHook != null ? activeHook.getAllTeams() : Collections.emptyList();
    }

    @Override
    public Optional<KothTeam> getTeamByLeader(UUID leader) {
        TeamHook activeHook = getActiveHook();
        return activeHook != null ? activeHook.getTeamByLeader(leader) : Optional.empty();
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
    public String getTeamDisplayName(KothTeam team) {
        return team.getDisplayName();
    }
    
    public String getActivePlugin() {
        TeamHook activeHook = getActiveHook();
        return activeHook != null ? activeHook.getPluginName() : "None";
    }
    
    public InternalTeamTracker getInternalTracker() {
        return internalTracker;
    }
    
    public boolean isUsingExternalPlugin() {
        TeamHook activeHook = getActiveHook();
        return activeHook != null && !(activeHook instanceof InternalTeamHook);
    }
}
