package dev.smartshub.shkoth.service.bossbar;

import dev.smartshub.shkoth.api.config.ConfigContainer;
import dev.smartshub.shkoth.api.koth.Koth;
import dev.smartshub.shkoth.message.MessageParser;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AdventureBossbarService {

    private static final PlainTextComponentSerializer PLAIN_SERIALIZER = PlainTextComponentSerializer.plainText();

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

        if (isTitleEmpty(title)) {
            return;
        }

        BossBar.Color color = getColorForProgress(koth.getCaptureProgress());
        float progress = Math.max(0.0f, Math.min(1.0f, koth.getCaptureProgress() / 100.0f));

        BossBar bossbar = BossBar.bossBar(title, progress, color, BossBar.Overlay.PROGRESS);
        activeBossbars.put(kothId, bossbar);

        addRelevantPlayers(kothId, koth, bossbar);
    }

    public void stopBossbars(Koth koth) {
        if (koth == null) {
            return;
        }

        String kothId = koth.getId();
        stopBossbarById(kothId);
    }

    public void refreshBossbars() {
        activeBossbars.keySet().forEach(this::updateSingleBossbar);
    }

    private void updateSingleBossbar(String kothId) {
        BossBar bossbar = activeBossbars.get(kothId);
        if (bossbar == null) {
            return;
        }

        Koth koth = getKothById(kothId);
        if (koth == null || !koth.isRunning()) {
            stopBossbarById(kothId);
            return;
        }

        Component newTitle = buildTitle(koth);

        if (isTitleEmpty(newTitle)) {
            stopBossbarById(kothId);
            return;
        }

        int captureProgress = koth.getCaptureProgress();
        bossbar.name(newTitle);
        bossbar.progress(Math.max(0.0f, Math.min(1.0f, captureProgress / 100.0f)));
        bossbar.color(getColorForProgress(captureProgress));

        updateBossbarPlayers(kothId, koth, bossbar);
    }

    private Component buildTitle(Koth koth) {
        String configKey = koth.isCapturing() ? "capturing-title" : "waiting-title";
        String configTitle = config.getString(configKey, "");

        Player contextPlayer = koth.isCapturing() ? koth.getCurrentCapturerPlayer() : null;
        return parser.parseWithPlayer(configTitle, contextPlayer);
    }

    private boolean isTitleEmpty(Component title) {
        if (title == null) {
            return true;
        }

        String plainText = PLAIN_SERIALIZER.serialize(title);
        return plainText.trim().isEmpty();
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

    private void addRelevantPlayers(String kothId, Koth koth, BossBar bossbar) {
        Set<Player> players = getPlayersForScope(koth);

        players.forEach(player -> player.showBossBar(bossbar));
        bossbarPlayers.put(kothId, players);
    }

    private void updateBossbarPlayers(String kothId, Koth koth, BossBar bossbar) {
        Set<Player> currentPlayers = bossbarPlayers.get(kothId);
        if (currentPlayers == null) {
            return;
        }

        Set<Player> shouldHaveBossbar = getPlayersForScope(koth);

        currentPlayers.removeIf(player -> {
            if (!shouldHaveBossbar.contains(player) || !player.isOnline()) {
                player.hideBossBar(bossbar);
                return true;
            }
            return false;
        });

        shouldHaveBossbar.forEach(player -> {
            if (currentPlayers.add(player)) {
                player.showBossBar(bossbar);
            }
        });
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

    private void stopBossbarById(String kothId) {
        BossBar bossbar = activeBossbars.remove(kothId);
        Set<Player> players = bossbarPlayers.remove(kothId);

        if (bossbar != null && players != null) {
            players.stream()
                    .filter(Player::isOnline)
                    .forEach(player -> player.hideBossBar(bossbar));
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

        if (bossbar != null && players != null && players.add(player)) {
            player.showBossBar(bossbar);
        }
    }

    public void removePlayerFromBossbar(String kothId, Player player) {
        BossBar bossbar = activeBossbars.get(kothId);
        Set<Player> players = bossbarPlayers.get(kothId);

        if (bossbar != null && players != null && players.remove(player)) {
            player.hideBossBar(bossbar);
        }
    }

    public void removeAllBossbars() {
        activeBossbars.forEach((kothId, bossbar) -> {
            Set<Player> players = bossbarPlayers.get(kothId);
            if (players != null) {
                players.stream()
                        .filter(Player::isOnline)
                        .forEach(player -> player.hideBossBar(bossbar));
            }
        });

        activeBossbars.clear();
        bossbarPlayers.clear();
    }
}