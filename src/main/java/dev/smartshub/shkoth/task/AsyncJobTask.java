package dev.smartshub.shkoth.task;

import dev.smartshub.shkoth.service.bossbar.AdventureBossbarService;
import dev.smartshub.shkoth.service.scoreboard.ScoreboardHandleService;
import dev.smartshub.shkoth.team.ContextualTeamTracker;
import org.bukkit.scheduler.BukkitRunnable;

public class AsyncJobTask extends BukkitRunnable {

    private final ScoreboardHandleService scoreboardHandleService;
    private final ContextualTeamTracker contextualTeamTracker;
    private final AdventureBossbarService adventureBossbarService;

    public AsyncJobTask(ScoreboardHandleService scoreboardHandleService, ContextualTeamTracker contextualTeamTracker,
                        AdventureBossbarService adventureBossbarService) {
        this.scoreboardHandleService = scoreboardHandleService;
        this.contextualTeamTracker = contextualTeamTracker;
        this.adventureBossbarService = adventureBossbarService;
    }

    @Override
    public void run() {
        scoreboardHandleService.handleAll();
        contextualTeamTracker.updateTeams();
        adventureBossbarService.refreshBossbars();
    }
}
