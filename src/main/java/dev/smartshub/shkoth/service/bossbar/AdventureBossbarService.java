package dev.smartshub.shkoth.service.bossbar;

import dev.smartshub.shkoth.api.config.ConfigContainer;
import dev.smartshub.shkoth.api.koth.Koth;
import dev.smartshub.shkoth.message.MessageParser;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AdventureBossbarService {

    //TODO: if title is empty, don't show bossbar

    private final ConfigContainer config;
    private final MessageParser parser;

    private final Map<String, BossBar> activeBossbars = new ConcurrentHashMap<>();
    private final Map<String, Set<Player>> bossbarPlayers = new ConcurrentHashMap<>();

    public AdventureBossbarService(ConfigContainer config, MessageParser parser) {
        this.config = config;
        this.parser = parser;
    }

    public void startBossbars(Koth koth) {
        if (koth == null || !koth.isRunning()) {
            return;
        }

        String kothId = koth.getId();
        Component title = buildTitle(koth);
        BossBar.Color color = getColorForProgress(koth.getCaptureProgress());
        float progress = Math.max(0.0f, Math.min(1.0f, koth.getCaptureProgress() / 100.0f));

        BossBar bossbar = BossBar.bossBar(title, progress, color, BossBar.Overlay.PROGRESS);
        activeBossbars.put(kothId, bossbar);

        addRelevantPlayers(kothId, koth);
    }

    public void stopBossbars(Koth koth) {
        if (koth == null) {
            return;
        }

        String kothId = koth.getId();
        BossBar bossbar = activeBossbars.remove(kothId);
        Set<Player> players = bossbarPlayers.remove(kothId);

        if (bossbar != null && players != null) {
            for (Player player : players) {
                if (player.isOnline()) {
                    player.hideBossBar(bossbar);
                }
            }
        }
    }

    public void refreshBossbars() {
        for (String kothId : Set.copyOf(activeBossbars.keySet())) {
            updateSingleBossbar(kothId);
        }
    }

    private void updateSingleBossbar(String kothId) {
        BossBar bossbar = activeBossbars.get(kothId);
        if (bossbar == null) {
            return;
        }

        Koth koth = getKothById(kothId);
        if (koth == null || !koth.isRunning()) {
            stopBossbars(koth);
            return;
        }

        Component newTitle = buildTitle(koth);
        float newProgress = Math.max(0.0f, Math.min(1.0f, koth.getCaptureProgress() / 100.0f));
        BossBar.Color newColor = getColorForProgress(koth.getCaptureProgress());

        bossbar.name(newTitle);
        bossbar.progress(newProgress);
        bossbar.color(newColor);

        updateBossbarPlayers(kothId, koth, bossbar);
    }

    private Component buildTitle(Koth koth) {
        String configTitle;

        if (koth.isCapturing()) {
            configTitle = config.getString("capturing-title", "");
        } else {
            configTitle = config.getString("waiting-title", "");
        }

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

    private void addRelevantPlayers(String kothId, Koth koth) {
        BossBar bossbar = activeBossbars.get(kothId);
        if (bossbar == null) return;

        Set<Player> players = ConcurrentHashMap.newKeySet();
        String playerScope = config.getString("player-scope", "world");

        switch (playerScope.toLowerCase()) {
            case "all":
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.showBossBar(bossbar);
                    players.add(player);
                }
                break;
            case "world":
                for (Player player : koth.getArea().getCenter().getWorld().getPlayers()) {
                    player.showBossBar(bossbar);
                    players.add(player);
                }
                break;
            case "area":
                for (Player player : koth.getArea().getCenter().getWorld().getPlayers()) {
                    if (koth.getArea().contains(player.getLocation())) {
                        player.showBossBar(bossbar);
                        players.add(player);
                    }
                }
                break;
        }

        bossbarPlayers.put(kothId, players);
    }

    private void updateBossbarPlayers(String kothId, Koth koth, BossBar bossbar) {
        Set<Player> currentPlayers = bossbarPlayers.computeIfAbsent(kothId, k -> ConcurrentHashMap.newKeySet());

        String playerScope = config.getString("player-scope", "world");
        Set<Player> shouldHaveBossbar = ConcurrentHashMap.newKeySet();

        switch (playerScope.toLowerCase()) {
            case "all":
                shouldHaveBossbar.addAll(Bukkit.getOnlinePlayers());
                break;
            case "world":
                shouldHaveBossbar.addAll(koth.getArea().getCenter().getWorld().getPlayers());
                break;
            case "area":
                for (Player player : koth.getArea().getCenter().getWorld().getPlayers()) {
                    if (koth.getArea().contains(player.getLocation())) {
                        shouldHaveBossbar.add(player);
                    }
                }
                break;
        }

        for (Player player : Set.copyOf(currentPlayers)) {
            if (!shouldHaveBossbar.contains(player) || !player.isOnline()) {
                player.hideBossBar(bossbar);
                currentPlayers.remove(player);
            }
        }

        for (Player player : shouldHaveBossbar) {
            if (!currentPlayers.contains(player)) {
                player.showBossBar(bossbar);
                currentPlayers.add(player);
            }
        }
    }

    private Koth getKothById(String kothId) {
        return null;
    }

    public boolean hasBossbar(String kothId) {
        return activeBossbars.containsKey(kothId);
    }

    public void addPlayerToBossbar(String kothId, Player player) {
        BossBar bossbar = activeBossbars.get(kothId);
        Set<Player> players = bossbarPlayers.get(kothId);

        if (bossbar != null && players != null && !players.contains(player)) {
            player.showBossBar(bossbar);
            players.add(player);
        }
    }

    public void removePlayerFromBossbar(String kothId, Player player) {
        BossBar bossbar = activeBossbars.get(kothId);
        Set<Player> players = bossbarPlayers.get(kothId);

        if (bossbar != null && players != null && players.contains(player)) {
            player.hideBossBar(bossbar);
            players.remove(player);
        }
    }

    public void removeAllBossbars() {
        for (String kothId : Set.copyOf(activeBossbars.keySet())) {
            BossBar bossbar = activeBossbars.get(kothId);
            Set<Player> players = bossbarPlayers.get(kothId);

            if (bossbar != null && players != null) {
                for (Player player : players) {
                    if (player.isOnline()) {
                        player.hideBossBar(bossbar);
                    }
                }
            }
        }
        activeBossbars.clear();
        bossbarPlayers.clear();
    }
}