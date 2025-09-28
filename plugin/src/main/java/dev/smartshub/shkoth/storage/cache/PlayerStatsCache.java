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
    private final ConcurrentHashMap<String, CompletableFuture<Integer>> pendingRequests = new ConcurrentHashMap<>();
    private final long ttl = TimeUnit.SECONDS.toMillis(30);

    public PlayerStatsCache(PlayerStatsDAO dao) {
        this.dao = dao;
    }

    public String getStat(Player player, StatType type) {
        String key = key(player.getUniqueId(), type);
        CachedValue cached = cache.get(key);

        if (cached != null && !cached.isExpired()) {
            return String.valueOf(cached.value);
        }

        CompletableFuture<Integer> pending = pendingRequests.get(key);
        if (pending != null && !pending.isDone()) {
            return cached != null ? String.valueOf(cached.value) : "0";
        }

        CompletableFuture<Integer> future = getFutureFor(type, player.getUniqueId());
        pendingRequests.put(key, future);

        future.thenAccept(value -> {
            cache.put(key, new CachedValue(value, System.currentTimeMillis()));
            pendingRequests.remove(key);
        }).exceptionally(throwable -> {
            pendingRequests.remove(key);
            return null;
        });

        return cached != null ? String.valueOf(cached.value) : "0";
    }


    public void preload(Player player) {
        dao.getPlayerStats(player.getUniqueId()).thenAccept(optStats -> {
            if (optStats.isPresent()) {
                UUID id = player.getUniqueId();
                long now = System.currentTimeMillis();
                var stats = optStats.get();

                cache.put(key(id, StatType.TOTAL), new CachedValue(stats.soloWins() + stats.teamWins(), now));
                cache.put(key(id, StatType.SOLO), new CachedValue(stats.soloWins(), now));
                cache.put(key(id, StatType.TEAM), new CachedValue(stats.teamWins(), now));
            } else {
                UUID id = player.getUniqueId();
                long now = System.currentTimeMillis();
                cache.put(key(id, StatType.TOTAL), new CachedValue(0, now));
                cache.put(key(id, StatType.SOLO), new CachedValue(0, now));
                cache.put(key(id, StatType.TEAM), new CachedValue(0, now));
            }
        });
    }

    public void invalidate(UUID playerId) {
        for (StatType type : StatType.values()) {
            String key = key(playerId, type);
            cache.remove(key);
            CompletableFuture<Integer> pending = pendingRequests.remove(key);
            if (pending != null && !pending.isDone()) {
                pending.cancel(false);
            }
        }
    }

    public void clear() {
        cache.clear();
        pendingRequests.values().forEach(future -> {
            if (!future.isDone()) {
                future.cancel(false);
            }
        });
        pendingRequests.clear();
    }

    public void refreshPlayer(UUID playerId) {
        invalidate(playerId);
        org.bukkit.Bukkit.getPlayer(playerId);
        if (org.bukkit.Bukkit.getPlayer(playerId) != null) {
            preload(org.bukkit.Bukkit.getPlayer(playerId));
        }
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