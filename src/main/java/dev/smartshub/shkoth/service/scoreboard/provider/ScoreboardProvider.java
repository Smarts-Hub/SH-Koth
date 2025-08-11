package dev.smartshub.shkoth.service.scoreboard.provider;

import java.util.List;

public interface ScoreboardProvider {
    List<String> getLines();
    String getTitle();
}
