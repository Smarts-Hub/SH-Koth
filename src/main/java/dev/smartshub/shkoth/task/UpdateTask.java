package dev.smartshub.shkoth.task;

import dev.smartshub.shkoth.service.koth.RefreshInsideKothService;
import dev.smartshub.shkoth.koth.ticking.KothTicker;
import dev.smartshub.shkoth.service.schedule.KothSchedulerService;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateTask extends BukkitRunnable {

    private final KothTicker capturedKothsHandler;
    private final RefreshInsideKothService refreshInsideKothService;
    private final KothSchedulerService kothSchedulerService;

    public UpdateTask(KothTicker capturedKothsHandler, RefreshInsideKothService refreshInsideKothService, KothSchedulerService kothSchedulerService) {
        this.capturedKothsHandler = capturedKothsHandler;
        this.refreshInsideKothService = refreshInsideKothService;
        this.kothSchedulerService = kothSchedulerService;
    }

    @Override
    public void run() {
        capturedKothsHandler.handleTickForCapturedKoths();
        refreshInsideKothService.refreshInsideKoth();
        kothSchedulerService.processAllSchedulesAndExecute();
        //TODO: Bossbar animations update (firstly implement the bossbar service)
    }
}
