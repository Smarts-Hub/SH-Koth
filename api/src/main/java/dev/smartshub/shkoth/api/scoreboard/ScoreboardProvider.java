package dev.smartshub.shkoth.api.scoreboard;

import java.util.List;

public interface ScoreboardProvider {
    List<String> getLines();
    String getTitle();
}
