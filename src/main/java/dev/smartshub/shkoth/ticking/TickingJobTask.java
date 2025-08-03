package dev.smartshub.shkoth.ticking;

import org.bukkit.scheduler.BukkitRunnable;

public class TickingJobTask extends BukkitRunnable {

    private final TickingKoth capturedKothsHandler;

    public TickingJobTask(TickingKoth capturedKothsHandler) {
        this.capturedKothsHandler = capturedKothsHandler;
    }

    @Override
    public void run() {
        capturedKothsHandler.handleTickForCapturedKoths();
    }
}
