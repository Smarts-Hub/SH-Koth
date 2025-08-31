package dev.smartshub.shkoth.task;

import dev.smartshub.shkoth.service.scoreboard.ScoreboardHandleService;
import dev.smartshub.shkoth.team.ContextualTeamTracker;
import org.bukkit.scheduler.BukkitRunnable;

public class AsyncJobTask extends BukkitRunnable {

    private final ScoreboardHandleService scoreboardHandleService;
    private final ContextualTeamTracker contextualTeamTracker;

    public AsyncJobTask(ScoreboardHandleService scoreboardHandleService, ContextualTeamTracker contextualTeamTracker) {
        this.scoreboardHandleService = scoreboardHandleService;
        this.contextualTeamTracker = contextualTeamTracker;
    }

    @Override
    public void run() {
        scoreboardHandleService.handleAll();
        contextualTeamTracker.updateTeams();
    }
}
