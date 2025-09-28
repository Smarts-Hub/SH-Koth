package dev.smartshub.shkoth.koth.tally.score;

import dev.smartshub.shkoth.api.koth.Koth;
import dev.smartshub.shkoth.api.koth.tally.Tally;
import dev.smartshub.shkoth.api.team.TeamWrapper;
import dev.smartshub.shkoth.team.ContextualTeamTracker;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ScoreTally implements Tally {
    private final Koth koth;
    private final Map<TeamWrapper, Integer> scores;

    public ScoreTally(Koth koth) {
        this.koth = koth;
        this.scores = new HashMap<>();
    }

    @Override
    public void handle() {
        ContextualTeamTracker tracker = (ContextualTeamTracker) koth.getTeamTracker();

        koth.getPlayersInside().stream()
                .map(uuid -> tracker.getTeamForKoth(uuid, koth.isSolo()))
                .filter(Objects::nonNull)
                .distinct()
                .forEach(team -> scores.merge(team, 1, Integer::sum));
    }

    public Map<TeamWrapper, Integer> getScores() {
        return new HashMap<>(scores);
    }
}
