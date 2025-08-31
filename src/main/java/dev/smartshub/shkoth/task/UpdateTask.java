package dev.smartshub.shkoth.task;

import dev.smartshub.shkoth.service.koth.RefreshInsideKothService;
import dev.smartshub.shkoth.koth.ticking.KothTicker;
import dev.smartshub.shkoth.service.schedule.KothSchedulerService;
import dev.smartshub.shkoth.service.scoreboard.ScoreboardHandleService;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.CompletableFuture;

public class UpdateTask extends BukkitRunnable {

    private final KothTicker capturedKothsHandler;
    private final RefreshInsideKothService refreshInsideKothService;
    private final KothSchedulerService kothSchedulerService;
    private final ScoreboardHandleService scoreboardHandleService;

    public UpdateTask(KothTicker capturedKothsHandler, RefreshInsideKothService refreshInsideKothService,
                      KothSchedulerService kothSchedulerService, ScoreboardHandleService scoreboardHandleService) {
        this.capturedKothsHandler = capturedKothsHandler;
        this.refreshInsideKothService = refreshInsideKothService;
        this.kothSchedulerService = kothSchedulerService;
        this.scoreboardHandleService = scoreboardHandleService;
    }

    @Override
    public void run() {
        capturedKothsHandler.handleTickForCapturedKoths();
        refreshInsideKothService.refreshInsideKoth();
        kothSchedulerService.processAllSchedulesAndExecute();
    }
}

