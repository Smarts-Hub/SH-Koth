package dev.smartshub.shkoth.listener.koth;

import dev.smartshub.shkoth.api.event.koth.KothEndEvent;
import dev.smartshub.shkoth.hook.placeholder.PlaceholderAPIHook;
import dev.smartshub.shkoth.service.notify.NotifyService;
import dev.smartshub.shkoth.storage.database.dao.PlayerStatsDAO;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class KothEndListener implements Listener {

    private final NotifyService notifyService;
    private final PlayerStatsDAO playerStatsDAO;

    public KothEndListener(NotifyService notifyService, PlayerStatsDAO playerStatsDAO) {
        this.notifyService = notifyService;
        this.playerStatsDAO = playerStatsDAO;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKothEnd(KothEndEvent event) {
        PlaceholderAPIHook.pushArgs(event.getKoth().getDisplayName());
        notifyService.sendBroadcastListToOnlinePlayers("koth.end");

        if(event.getReason() != KothEndEvent.EndReason.CAPTURE_COMPLETED) return;

        event.getWinners().forEach(player -> {
            if(event.getKoth().isSolo()){
                playerStatsDAO.increaseSoloWin(player);
            } else {
                playerStatsDAO.increaseTeamWin(player);
            }
        });
    }

}
