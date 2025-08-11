package dev.smartshub.shkoth.listener.koth;

import dev.smartshub.shkoth.api.event.koth.KothStateChangeEvent;
import dev.smartshub.shkoth.handler.ScoreboardHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class KothStateChangeListener implements Listener {

    private final ScoreboardHandler scoreboardHandler;

    public KothStateChangeListener(ScoreboardHandler scoreboardHandler) {
        this.scoreboardHandler = scoreboardHandler;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKothStateChange(KothStateChangeEvent event) {
        scoreboardHandler.handleChange(event.getKoth());
    }
}
