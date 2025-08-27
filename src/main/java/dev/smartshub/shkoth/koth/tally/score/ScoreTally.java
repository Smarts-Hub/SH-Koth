package dev.smartshub.shkoth.koth.tally.score;

import dev.smartshub.shkoth.api.koth.Koth;
import dev.smartshub.shkoth.api.koth.tally.Tally;
import dev.smartshub.shkoth.api.team.KothTeam;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ScoreTally implements Tally {

    private final Koth koth;
    private final Map<KothTeam, Integer> scores;

    public ScoreTally(Koth koth) {
        this.koth = koth;
        this.scores = koth.getTeamTracker().getAllTeams().stream()
                .collect(Collectors.toMap(
                        team -> team,
                        team -> 0
                ));
    }

    @Override
    public void handle() {
        koth.getPlayersInside().stream()
                .map(uuid -> koth.getTeamTracker().getTeamFrom(uuid))
                .filter(Objects::nonNull)
                .distinct()
                .forEach(team -> scores.merge(team, 1, Integer::sum));
    }

}

