package dev.smartshub.shkoth.service.bossbar;

import dev.smartshub.shkoth.api.config.ConfigContainer;
import dev.smartshub.shkoth.api.koth.Koth;
import dev.smartshub.shkoth.message.MessageParser;
import dev.smartshub.shkoth.registry.KothRegistry;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AdventureBossbarService {

    private final KothRegistry kothRegistry;
    private final ConfigContainer config;
    private final MessageParser parser;

    private static class PlayerBossbarInfo {
        final BossBar bossbar;
        String currentKothId;

        PlayerBossbarInfo(BossBar bossbar, String kothId) {
            this.bossbar = bossbar;
            this.currentKothId = kothId;
        }
    }

    private final Map<UUID, PlayerBossbarInfo> playerBossbars = new ConcurrentHashMap<>();
    private final Set<String> activeKoths = ConcurrentHashMap.newKeySet();

    public AdventureBossbarService(KothRegistry kothRegistry,ConfigContainer config, MessageParser parser) {
        this.kothRegistry = kothRegistry;
        this.config = config;
        this.parser = parser;
    }

    public void startBossbars(Koth koth) {
        if (koth == null || !koth.isRunning()) {
            return;
        }

        activeKoths.add(koth.getId());
        updateBossbarsForKoth(koth);
    }

    public void stopBossbars(Koth koth) {
        if (koth == null) {
            return;
        }

        String kothId = koth.getId();
        activeKoths.remove(kothId);

        playerBossbars.entrySet().removeIf(entry -> {
            PlayerBossbarInfo info = entry.getValue();
            if (kothId.equals(info.currentKothId)) {
                Player player = Bukkit.getPlayer(entry.getKey());
                if (player != null && player.isOnline()) {
                    player.hideBossBar(info.bossbar);
                }
                return true;
            }
            return false;
        });
    }

    public void refreshBossbars() {
        for (String kothId : activeKoths) {
            Koth koth = getKothById(kothId);
            if (koth != null && koth.isRunning()) {
                updateBossbarsForKoth(koth);
            } else {
                activeKoths.remove(kothId);
            }
        }
    }

    private void updateBossbarsForKoth(Koth koth) {
        String kothId = koth.getId();
        Component title = buildTitle(koth);

        if (title.children().isEmpty()) {
            hideKothFromPlayers(kothId);
            return;
        }

        Set<Player> targetPlayers = getPlayersForScope(koth);

        int captureProgress = koth.getCaptureProgress();
        BossBar.Color color = getColorForProgress(captureProgress);
        float progress = Math.max(0.0f, Math.min(1.0f, captureProgress / 100.0f));

        for (Player player : targetPlayers) {
            UUID playerId = player.getUniqueId();
            PlayerBossbarInfo info = playerBossbars.get(playerId);

            if (info == null) {
                BossBar bossbar = BossBar.bossBar(title, progress, color, BossBar.Overlay.PROGRESS);
                info = new PlayerBossbarInfo(bossbar, kothId);
                playerBossbars.put(playerId, info);
                player.showBossBar(bossbar);
            } else {
                info.bossbar.name(title);
                info.bossbar.progress(progress);
                info.bossbar.color(color);
                info.currentKothId = kothId;
            }
        }

        Set<UUID> targetPlayerIds = Set.of(targetPlayers.stream()
                .map(Player::getUniqueId)
                .toArray(UUID[]::new));

        playerBossbars.entrySet().removeIf(entry -> {
            PlayerBossbarInfo info = entry.getValue();
            if (kothId.equals(info.currentKothId)) {
                UUID playerId = entry.getKey();
                Player player = Bukkit.getPlayer(playerId);

                if (player == null || !player.isOnline() || !targetPlayerIds.contains(playerId)) {
                    if (player != null && player.isOnline()) {
                        player.hideBossBar(info.bossbar);
                    }
                    return true;
                }
            }
            return false;
        });
    }

    private void hideKothFromPlayers(String kothId) {
        playerBossbars.entrySet().removeIf(entry -> {
            PlayerBossbarInfo info = entry.getValue();
            if (kothId.equals(info.currentKothId)) {
                Player player = Bukkit.getPlayer(entry.getKey());
                if (player != null && player.isOnline()) {
                    player.hideBossBar(info.bossbar);
                }
                return true;
            }
            return false;
        });
    }

    private Component buildTitle(Koth koth) {
        String configKey = koth.isCapturing() ? "capturing-title" : "waiting-title";
        String configTitle = config.getString(configKey, "");

        Player contextPlayer = koth.isCapturing() ? koth.getCurrentCapturerPlayer() : null;
        return parser.parseWithPlayer(configTitle, contextPlayer);
    }


    private BossBar.Color getColorForProgress(int progress) {
        String colorConfig = config.getString("color-by-progress", "auto");

        if (!"auto".equalsIgnoreCase(colorConfig)) {
            return parseColor(colorConfig);
        }

        if (progress >= 70) return BossBar.Color.GREEN;
        if (progress >= 30) return BossBar.Color.YELLOW;
        return BossBar.Color.RED;
    }

    private BossBar.Color parseColor(String colorString) {
        try {
            return BossBar.Color.valueOf(colorString.toUpperCase());
        } catch (IllegalArgumentException e) {
            return BossBar.Color.RED;
        }
    }

    private Set<Player> getPlayersForScope(Koth koth) {
        String playerScope = config.getString("player-scope", "world");
        Set<Player> players = ConcurrentHashMap.newKeySet();

        switch (playerScope.toLowerCase()) {
            case "all":
                players.addAll(Bukkit.getOnlinePlayers());
                break;
            case "world":
                players.addAll(koth.getArea().getCenter().getWorld().getPlayers());
                break;
            case "area":
                koth.getArea().getCenter().getWorld().getPlayers().stream()
                        .filter(player -> koth.getArea().contains(player.getLocation()))
                        .forEach(players::add);
                break;
        }

        return players;
    }

    private Koth getKothById(String kothId) {
        return kothRegistry.get(kothId);
    }

    public boolean hasBossbar(String kothId) {
        return activeKoths.contains(kothId);
    }

    public void addPlayerToBossbar(String kothId, Player player) {
        if (!activeKoths.contains(kothId)) return;

        Koth koth = getKothById(kothId);
        if (koth != null && koth.isRunning()) {
            updateBossbarsForKoth(koth);
        }
    }

    public void removePlayerFromBossbar(String kothId, Player player) {
        UUID playerId = player.getUniqueId();
        PlayerBossbarInfo info = playerBossbars.remove(playerId);

        if (info != null && kothId.equals(info.currentKothId)) {
            player.hideBossBar(info.bossbar);
        }
    }

    public void removeAllBossbars() {
        playerBossbars.forEach((playerId, info) -> {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                player.hideBossBar(info.bossbar);
            }
        });

        playerBossbars.clear();
        activeKoths.clear();
    }

    public void cleanupOfflinePlayers() {
        playerBossbars.keySet().removeIf(playerId ->
                Bukkit.getPlayer(playerId) == null);
    }
}