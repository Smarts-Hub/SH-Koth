package dev.smartshub.shkoth.service.scoreboard.provider;

import dev.smartshub.shkoth.api.config.ConfigContainer;
import dev.smartshub.shkoth.api.config.ConfigType;
import dev.smartshub.shkoth.api.koth.Koth;
import dev.smartshub.shkoth.api.scoreboard.ScoreboardProvider;
import dev.smartshub.shkoth.service.config.ConfigService;

import java.util.List;

public final class RunningScoreboard implements ScoreboardProvider {

    private final ConfigContainer container;

    public RunningScoreboard(ConfigService service, Koth koth) {
        this.container = service.provide(koth.getId() + ".yml", ConfigType.KOTH_DEFINITION);
    }

    @Override
    public List<String> getLines() {
        return container.getStringList("scoreboard.running.lines", List.of());
    }

    @Override
    public String getTitle() {
        return container.getString("scoreboard.running.title", "");
    }
}
