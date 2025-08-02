package dev.smartshub.shkoth.koth.timer;

import dev.smartshub.shkoth.koth.Koth;
import org.bukkit.scheduler.BukkitRunnable;

public class KothTimer extends BukkitRunnable {

    private final Koth koth;
    private long count = 0;

    public KothTimer(Koth koth) {
        this.koth = koth;
    }

    @Override
    public void run() {

    }
}
