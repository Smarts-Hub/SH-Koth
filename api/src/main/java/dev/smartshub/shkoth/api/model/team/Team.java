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
    public static Team solo(UUID player) {
        return new Team(player, Set.of(player), 1);
    }

    public static Team withLeader(UUID leader) {
        return new Team(leader, new HashSet<>(Set.of(leader)), 1);
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
                .collect(Collectors.toSet());
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
}
