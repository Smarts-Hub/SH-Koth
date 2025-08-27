package dev.smartshub.shkoth.api.team.hook.wrapper;

import dev.smartshub.shkoth.api.team.KothTeam;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class ExternalTeam implements KothTeam {
    private final UUID leader;
    private final Set<UUID> members;
    private final String displayName;
    private final String source;

    public ExternalTeam(UUID leader, Set<UUID> members, String displayName, String source) {
        this.leader = leader;
        this.members = Set.copyOf(members);
        this.displayName = displayName;
        this.source = source;
    }

    @Override
    public UUID getLeader() {
        return leader;
    }

    @Override
    public Set<UUID> getMembers() {
        return members;
    }

    @Override
    public boolean contains(UUID playerId) {
        return members.contains(playerId);
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    public String getSource() {
        return source;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof KothTeam other)) return false;
        return Objects.equals(leader, other.getLeader()) &&
               Objects.equals(members, other.getMembers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(leader, members);
    }
}