package dev.smartshub.shkoth.api.model.team;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public record Team(
        UUID leader,
        Set<UUID> members,
        int size
) {

    //TODO: better implementation (ignoring solo) and think about logic for team creation:
    // U enter to a koth with no team and what happens?

    public Team {
        if (leader == null) {
            throw new IllegalArgumentException("Leader cannot be null");
        }
        if (members == null) {
            members = Set.of(leader);
        }
        if (!members.contains(leader)) {
            Set<UUID> newMembers = new HashSet<>(members);
            newMembers.add(leader);
            members = Set.copyOf(newMembers);
        }
        members = Set.copyOf(members);
        size = members.size();
    }

    // Factory methods

    public static Team withLeader(UUID leader) {
        return new Team(leader, Set.of(leader), 1);
    }

    public static Team of(UUID leader, UUID... members) {
        Set<UUID> allMembers = new HashSet<>();
        allMembers.add(leader);
        allMembers.addAll(Arrays.asList(members));
        return new Team(leader, allMembers, allMembers.size());
    }


    public Team addMember(UUID newMember) {
        if (newMember == null) {
            throw new IllegalArgumentException("Member cannot be null");
        }

        if (members.contains(newMember)) {
            return this;
        }

        Set<UUID> newMembers = new HashSet<>(members);
        newMembers.add(newMember);

        return new Team(leader, newMembers, newMembers.size());
    }


    public Team removeMember(UUID memberToRemove) {
        if (memberToRemove == null || !members.contains(memberToRemove)) {
            return this;
        }

        if (leader.equals(memberToRemove)) {
            return null;
        }

        Set<UUID> newMembers = new HashSet<>(members);
        newMembers.remove(memberToRemove);

        return new Team(leader, newMembers, newMembers.size());
    }


    public Team changeLeader(UUID newLeader) {
        if (newLeader == null) {
            throw new IllegalArgumentException("New leader cannot be null");
        }

        if (!members.contains(newLeader)) {
            throw new IllegalArgumentException("New leader must be a current team member");
        }

        if (leader.equals(newLeader)) {
            return this;
        }

        return new Team(newLeader, members, size);
    }


    public Team addMembers(UUID... newMembers) {
        return addMembers(Arrays.asList(newMembers));
    }

    public Team addMembers(Collection<UUID> newMembers) {
        if (newMembers == null || newMembers.isEmpty()) {
            return this;
        }

        Set<UUID> updatedMembers = new HashSet<>(members);
        boolean changed = updatedMembers.addAll(newMembers);

        if (!changed) {
            return this;
        }

        return new Team(leader, updatedMembers, updatedMembers.size());
    }


    public Team removeMembers(UUID... membersToRemove) {
        return removeMembers(Arrays.asList(membersToRemove));
    }

    public Team removeMembers(Collection<UUID> membersToRemove) {
        if (membersToRemove == null || membersToRemove.isEmpty()) {
            return this;
        }

        if (membersToRemove.contains(leader)) {
            return null;
        }

        Set<UUID> updatedMembers = new HashSet<>(members);
        boolean changed = updatedMembers.removeAll(membersToRemove);

        if (!changed) {
            return this;
        }

        return new Team(leader, updatedMembers, updatedMembers.size());
    }


    public boolean contains(UUID player) {
        return members.contains(player);
    }

    public boolean isLeader(UUID player) {
        return leader.equals(player);
    }

    public Set<UUID> getMembersExcludingLeader() {
        return members.stream()
                .filter(uuid -> !uuid.equals(leader))
                .collect(Collectors.toUnmodifiableSet());
    }

    public List<Player> getOnlineMembers() {
        return members.stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Player getLeaderPlayer() {
        return Bukkit.getPlayer(leader);
    }

    public boolean isValid() {
        return leader != null && members != null && members.contains(leader) && members.size() == size;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Team other)) return false;

        return Objects.equals(leader, other.leader) &&
                Objects.equals(members, other.members);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leader, members);
    }

}
