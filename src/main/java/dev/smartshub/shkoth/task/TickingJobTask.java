package dev.smartshub.shkoth.task;

import dev.smartshub.shkoth.handler.CapturingKothHandler;
import org.bukkit.scheduler.BukkitRunnable;

public class TickingJobTask extends BukkitRunnable {

    private final CapturingKothHandler capturedKothsHandler;

    public TickingJobTask(CapturingKothHandler capturedKothsHandler) {
        this.capturedKothsHandler = capturedKothsHandler;
    }

    @Override
    public void run() {
        capturedKothsHandler.handleTickForCapturedKoths();
    }
}
