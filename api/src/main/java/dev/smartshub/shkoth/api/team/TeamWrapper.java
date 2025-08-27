package dev.smartshub.shkoth.api.team;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


public class TeamWrapper implements KothTeam {
    private final UUID representativePlayer;
    private final Set<UUID> members;
    private final String displayName;
    private final boolean isInternal;
    
    public TeamWrapper(UUID representativePlayer, Set<UUID> members, String displayName) {
        this.representativePlayer = representativePlayer;
        this.members = new HashSet<>(members);
        this.displayName = displayName;
        this.isInternal = false;
    }
    
    public TeamWrapper(KothTeam internalTeam) {
        this.representativePlayer = internalTeam.getLeader();
        this.members = new HashSet<>(internalTeam.getMembers());
        this.displayName = internalTeam.getDisplayName();
        this.isInternal = true;
    }
    
    @Override
    public UUID getLeader() {
        return representativePlayer;
    }
    
    @Override
    public Set<UUID> getMembers() {
        return new HashSet<>(members);
    }
    
    @Override
    public boolean contains(UUID playerId) {
        return members.contains(playerId);
    }
    
    @Override
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isInternal() {
        return isInternal;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TeamWrapper)) return false;
        TeamWrapper other = (TeamWrapper) obj;
        return Objects.equals(this.members, other.members);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(members);
    }
}
