package dev.smartshub.shkoth.listener.koth;

import dev.smartshub.shkoth.api.event.koth.KothStateChangeEvent;
import dev.smartshub.shkoth.service.scoreboard.ScoreboardHandleService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class KothStateChangeListener implements Listener {

    private final ScoreboardHandleService scoreboardHandleService;

    public KothStateChangeListener(ScoreboardHandleService scoreboardHandleService) {
        this.scoreboardHandleService = scoreboardHandleService;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKothStateChange(KothStateChangeEvent event) {
        if(event.isCancelled() || !event.getKoth().isScoreboardEnabled()) return;
        scoreboardHandleService.handleChange(event.getKoth(), event.getNewState());
    }
}
