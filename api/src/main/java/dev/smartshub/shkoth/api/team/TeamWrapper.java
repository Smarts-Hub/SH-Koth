package dev.smartshub.shkoth.api.team;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


public class TeamWrapper implements KothTeam {
    private final UUID representativePlayer;
    private Set<UUID> members;
    private String displayName;
    private final boolean isInternal;
    private final boolean isSoloMode;
    private long lastValidated;
    private boolean needsValidation;

    public TeamWrapper(UUID representativePlayer, Set<UUID> members, String displayName, boolean isSoloMode) {
        this.representativePlayer = representativePlayer;
        this.members = new HashSet<>(members);
        this.displayName = displayName;
        this.isInternal = false;
        this.isSoloMode = isSoloMode;
        this.lastValidated = System.currentTimeMillis();
        this.needsValidation = false;
    }

    public TeamWrapper(KothTeam internalTeam, boolean isSoloMode) {
        this.representativePlayer = internalTeam.getLeader();
        this.members = new HashSet<>(internalTeam.getMembers());
        this.displayName = internalTeam.getDisplayName();
        this.isInternal = true;
        this.isSoloMode = isSoloMode;
        this.lastValidated = System.currentTimeMillis();
        this.needsValidation = false;
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

    public boolean isSoloMode() {
        return isSoloMode;
    }

    public long getLastValidated() {
        return lastValidated;
    }

    public boolean needsValidation() {
        return needsValidation;
    }

    public void markForValidation() {
        this.needsValidation = true;
    }

    public void updateMembers(Set<UUID> newMembers, String newDisplayName) {
        this.members = new HashSet<>(newMembers);
        this.displayName = newDisplayName;
        this.lastValidated = System.currentTimeMillis();
        this.needsValidation = false;
    }

    public boolean isValidationExpired(long validationInterval) {
        return System.currentTimeMillis() - lastValidated > validationInterval;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TeamWrapper)) return false;
        TeamWrapper other = (TeamWrapper) obj;
        return Objects.equals(this.members, other.members) &&
                this.isSoloMode == other.isSoloMode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(members, isSoloMode);
    }
}
