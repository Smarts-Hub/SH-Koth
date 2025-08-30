package dev.smartshub.shkoth.storage.cache;

import dev.smartshub.shkoth.api.stat.StatType;
import dev.smartshub.shkoth.storage.database.dao.PlayerStatsDAO;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class PlayerStatsCache {

    private final PlayerStatsDAO dao;
    private final ConcurrentHashMap<String, CachedValue> cache = new ConcurrentHashMap<>();
    private final long ttl = TimeUnit.SECONDS.toMillis(30);

    public PlayerStatsCache(PlayerStatsDAO dao) {
        this.dao = dao;
    }

    public String getStat(Player player, StatType type) {
        return getCachedStat(player, type, getFutureFor(type, player.getUniqueId()));
    }

    public void preload(Player player) {
        dao.getPlayerStats(player.getUniqueId()).thenAccept(optStats -> {
            optStats.ifPresent(stats -> {
                UUID id = player.getUniqueId();
                long now = System.currentTimeMillis();

                cache.put(key(id, StatType.TOTAL), new CachedValue(stats.getTotalWins(), now));
                cache.put(key(id, StatType.SOLO), new CachedValue(stats.soloWins(), now));
                cache.put(key(id, StatType.TEAM), new CachedValue(stats.teamWins(), now));
            });
        });
    }

    public void invalidate(UUID playerId) {
        for (StatType type : StatType.values()) {
            cache.remove(key(playerId, type));
        }
    }

    public void clear() {
        cache.clear();
    }

    private String getCachedStat(Player player, StatType type, CompletableFuture<Integer> future) {
        String key = key(player.getUniqueId(), type);
        CachedValue cached = cache.get(key);

        if (cached != null && !cached.isExpired()) {
            return String.valueOf(cached.value);
        }

        future.thenAccept(value -> {
            System.out.println("[SHKoth] Caching " + type.name() + " wins for player " + player.getName() + ": " + value);
            cache.put(key, new CachedValue(value, System.currentTimeMillis()));
        });

        return cached != null ? String.valueOf(cached.value) : "0";
    }

    private CompletableFuture<Integer> getFutureFor(StatType type, UUID playerId) {
        return switch (type) {
            case TOTAL -> dao.getTotalWins(playerId);
            case SOLO  -> dao.getSoloWins(playerId);
            case TEAM  -> dao.getTeamWins(playerId);
        };
    }

    private String key(UUID id, StatType type) {
        return id + "_" + type.name();
    }

    private class CachedValue {
        final int value;
        final long timestamp;

        CachedValue(int value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }

        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > ttl;
        }
    }
}
