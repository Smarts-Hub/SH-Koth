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
        registerHook(new SimpleClansHook());

        registerHook(internalHook);
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

    private boolean isUsingInternalSystem() {
        TeamHook activeHook = getActiveHook();
        return activeHook instanceof InternalTeamHook;
    }

    @Override
    public Set<UUID> getTeamMembers(UUID anyTeamMember) {
        TeamHook activeHook = getActiveHook();
        return activeHook != null ? activeHook.getTeamMembers(anyTeamMember) : Set.of();
    }

    @Override
    public Optional<KothTeam> getTeamByLeader(UUID leader) {
        TeamHook activeHook = getActiveHook();
        return activeHook != null ? activeHook.getTeamLeader(leader) : Optional.empty();
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

    @Override
    public String getActiveProvider() {
        TeamHook activeHook = getActiveHook();
        return activeHook != null ? activeHook.getPluginName() : "None";
    }

    @Override
    public KothTeam createTeam(UUID leader) {
        if (!canCreateTeams()) {
            throw new UnsupportedOperationException("Cannot create teams with external plugin: " + getActiveProvider());
        }
        return internalHook.createTeam(leader);
    }

    @Override
    public boolean canCreateTeams() {
        return isUsingInternalSystem();
    }

    @Override
    public boolean canManageTeams() {
        return isUsingInternalSystem();
    }

    @Override
    public boolean addMemberToTeam(UUID member, UUID teamLeader) {
        if (!canManageTeams()) return false;
        return internalHook.addMemberToTeam(member, teamLeader);
    }

    @Override
    public boolean removeMemberFromTeam(UUID member) {
        if (!canManageTeams()) return false;
        return internalHook.removeMemberFromTeam(member);
    }

    @Override
    public boolean disbandTeam(UUID leader) {
        if (!canManageTeams()) return false;
        return internalHook.disbandTeam(leader);
    }

    @Override
    public boolean transferLeadership(UUID oldLeader, UUID newLeader) {
        if (!canManageTeams()) return false;
        return internalHook.transferLeadership(oldLeader, newLeader);
    }

    public InternalTeamHook getInternalHook() {
        return internalHook;
    }
}
