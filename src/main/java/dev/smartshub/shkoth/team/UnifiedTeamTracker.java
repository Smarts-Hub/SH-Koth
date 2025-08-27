package dev.smartshub.shkoth.team;

import dev.smartshub.shkoth.api.team.KothTeam;
import dev.smartshub.shkoth.api.team.TeamWrapper;
import dev.smartshub.shkoth.api.team.handle.TeamHandler;
import dev.smartshub.shkoth.api.team.hook.TeamHook;
import dev.smartshub.shkoth.api.team.track.TeamTracker;

import java.util.*;

public class UnifiedTeamTracker implements TeamTracker {
    
    private static UnifiedTeamTracker instance;
    private final List<TeamHook> hooks = new ArrayList<>();
    private final TeamHandler internalHandler;
    
    private UnifiedTeamTracker() {
        this.internalHandler = new InternalTeamHandler();
        setupHooks();
    }
    
    public static UnifiedTeamTracker getInstance() {
        if (instance == null) {
            instance = new UnifiedTeamTracker();
        }
        return instance;
    }
    
    private void setupHooks() {
        // TODO: register external hooks here
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

    public TeamWrapper getOrCreateTeamWrapper(UUID playerId, boolean isSoloMode) {
        TeamHook activeHook = getActiveHook();
        
        if (activeHook != null) {
            Set<UUID> members = activeHook.getTeamMembers(playerId);
            if (members.isEmpty()) {
                if (isSoloMode) {
                    members = Set.of(playerId);
                } else {
                    return null;
                }
            }
            
            String displayName = activeHook.getTeamDisplayName(playerId);
            return new TeamWrapper(playerId, members, displayName);
        } else {
            KothTeam internalTeam = internalHandler.getTeam(playerId);
            if (internalTeam == null && isSoloMode) {
                internalTeam = internalHandler.createTeam(playerId, 1);
            }
            
            return internalTeam != null ? new TeamWrapper(internalTeam) : null;
        }
    }

    public TeamWrapper getTeamWrapper(UUID playerId) {
        TeamHook activeHook = getActiveHook();
        
        if (activeHook != null) {
            Set<UUID> members = activeHook.getTeamMembers(playerId);
            if (members.isEmpty()) return null;
            
            String displayName = activeHook.getTeamDisplayName(playerId);
            return new TeamWrapper(playerId, members, displayName);
        } else {
            KothTeam internalTeam = internalHandler.getTeam(playerId);
            return internalTeam != null ? new TeamWrapper(internalTeam) : null;
        }
    }

    public TeamWrapper createInternalTeam(UUID leaderId, int maxMembers) {
        if (getActiveHook() != null) {
            return null;
        }

        KothTeam internalTeam = internalHandler.createTeam(leaderId, maxMembers);
        return internalTeam != null ? new TeamWrapper(internalTeam) : null;
    }
    
    public TeamHandler getInternalHandler() {
        return internalHandler;
    }
}