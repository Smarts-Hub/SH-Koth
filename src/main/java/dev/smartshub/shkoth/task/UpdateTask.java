package dev.smartshub.shkoth.task;

import dev.smartshub.shkoth.service.koth.RefreshInsideKothService;
import dev.smartshub.shkoth.koth.ticking.KothTicker;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateTask extends BukkitRunnable {

    private final KothTicker capturedKothsHandler;
    private final RefreshInsideKothService refreshInsideKothService;

    public UpdateTask(KothTicker capturedKothsHandler, RefreshInsideKothService refreshInsideKothService) {
        this.capturedKothsHandler = capturedKothsHandler;
        this.refreshInsideKothService = refreshInsideKothService;
    }

    @Override
    public void run() {
        capturedKothsHandler.handleTickForCapturedKoths();
        refreshInsideKothService.refreshInsideKoth();
        //TODO: Bossbar animations update (firstly implement the bossbar service)
    }
}
