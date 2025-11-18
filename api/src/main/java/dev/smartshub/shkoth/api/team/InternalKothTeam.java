package dev.smartshub.shkoth.api.team;

import org.bukkit.Bukkit;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class InternalKothTeam implements KothTeam {
    private final UUID teamId;
    private UUID leader;
    private final Set<UUID> members;
    private final int maxMembers;
    private String displayName;

    public InternalKothTeam(UUID leader, int maxMembers) {
        this.teamId = UUID.randomUUID();
        this.leader = leader;
        this.members = new HashSet<>();
        this.members.add(leader);
        this.maxMembers = maxMembers;
        this.displayName = generateDisplayName();
    }

    /**
     * Copy constructor to create a snapshot of a team's state.
     * 
     * @param other The team to copy.
     */
    public InternalKothTeam(InternalKothTeam other) {
        this.teamId = other.teamId;
        this.leader = other.leader;
        this.members = new HashSet<>(other.members);
        this.maxMembers = other.maxMembers;
        this.displayName = other.displayName;
    }

    @Override
    public UUID getLeader() {
        return leader;
    }

    @Override
    public Set<UUID> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    @Override
    public boolean contains(UUID playerId) {
        return members.contains(playerId);
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    public UUID getTeamId() {
        return teamId;
    }

    public boolean addMember(UUID member) {
        if (members.size() >= maxMembers) {
            return false;
        }
        members.add(member);
        return true;
    }

    public boolean removeMember(UUID member) {
        if (isLeader(member)) {
            return false;
        }
        return members.remove(member);
    }

    public boolean transferLeadership(UUID newLeader) {
        if (!members.contains(newLeader)) {
            return false;
        }
        this.leader = newLeader;
        updateDisplayName();
        return true;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public boolean isFull() {
        return members.size() >= maxMembers;
    }

    public boolean isEmpty() {
        return members.isEmpty();
    }

    private String generateDisplayName() {
        // Use getOfflinePlayer to safely get the name even if the leader is offline.
        String leaderName = Bukkit.getOfflinePlayer(leader).getName();
        if (leaderName == null) {
            leaderName = "Unknown"; // Fallback for very rare cases
        }
        return maxMembers == 1 ? leaderName : leaderName + "'s Team";
    }

    private void updateDisplayName() {
        this.displayName = generateDisplayName();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof InternalKothTeam))
            return false;
        InternalKothTeam other = (InternalKothTeam) obj;
        return Objects.equals(this.teamId, other.teamId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamId);
    }

    @Override
    public String toString() {
        return "InternalKothTeam{" +
                "teamId=" + teamId +
                ", leader=" + leader +
                ", members=" + members.size() +
                ", displayName='" + displayName + '\'' +
                ", maxMembers=" + maxMembers +
                '}';
    }
}