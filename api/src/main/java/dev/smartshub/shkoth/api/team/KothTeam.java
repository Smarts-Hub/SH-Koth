package dev.smartshub.shkoth.api.team;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public interface KothTeam {
    UUID getLeader();
    Set<UUID> getMembers();
    boolean contains(UUID playerId);
    String getDisplayName();
    
    default boolean isLeader(UUID playerId) {
        return getLeader().equals(playerId);
    }
    
    default List<Player> getOnlineMembers() {
        return getMembers().stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    default Player getLeaderPlayer() {
        return Bukkit.getPlayer(getLeader());
    }
}