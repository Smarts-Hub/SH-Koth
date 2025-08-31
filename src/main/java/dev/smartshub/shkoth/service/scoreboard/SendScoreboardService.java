package dev.smartshub.shkoth.service.scoreboard;

import dev.smartshub.shkoth.api.koth.Koth;
import dev.smartshub.shkoth.api.koth.guideline.KothState;
import dev.smartshub.shkoth.message.MessageParser;
import dev.smartshub.shkoth.service.config.ConfigService;
import dev.smartshub.shkoth.service.scoreboard.provider.CapturingScoreboard;
import dev.smartshub.shkoth.service.scoreboard.provider.RunningScoreboard;
import dev.smartshub.shkoth.api.scoreboard.ScoreboardProvider;
import fr.mrmicky.fastboard.adventure.FastBoard;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SendScoreboardService {

    private final Map<UUID, FastBoard> boards = new HashMap<>();
    private final Map<Koth, ScoreboardProvider> providers;
    private final ConfigService service;
    private final MessageParser parser;

    public SendScoreboardService(ConfigService service, MessageParser parser) {
        this.parser = parser;
        this.providers = new HashMap<>();
        this.service = service;
    }

    public void send(Player player, Koth koth, KothState state) {
        boards.computeIfAbsent(player.getUniqueId(),
                uuid -> new FastBoard(player));
        providers.computeIfAbsent(koth, k -> get(koth, state));
    }

    public void updateBoard(Player player, Koth koth, KothState state) {
        FastBoard board = boards.get(player.getUniqueId());
        if (board == null) return;

        ScoreboardProvider provider = providers.computeIfAbsent(koth, k -> get(koth, state));
        if(provider == null) return;

        board.updateTitle(parser.parseWithPlayer(provider.getTitle(), player));
        board.updateLines(parser.parseListWithPlayer(provider.getLines(), player));
    }

    public void updateAll(Koth koth, KothState state) {
        for (UUID uuid : boards.keySet()) {
            Player player = org.bukkit.Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                updateBoard(player, koth, state);
            }
        }
    }

    public void remove(Player player) {
        FastBoard board = boards.remove(player.getUniqueId());
        if(board == null) return;
        board.delete();
    }

    private ScoreboardProvider get(Koth koth, KothState state) {
        return switch (state) {
            case RUNNING -> new RunningScoreboard(service, koth);
            case CAPTURING -> new CapturingScoreboard(service, koth);
            default -> null;
        };
    }
}
