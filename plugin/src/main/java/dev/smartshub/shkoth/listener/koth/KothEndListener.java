package dev.smartshub.shkoth.listener.koth;

import dev.smartshub.shkoth.api.event.koth.KothEndEvent;
import dev.smartshub.shkoth.hook.placeholder.PlaceholderAPIHook;
import dev.smartshub.shkoth.service.notify.NotifyService;
import dev.smartshub.shkoth.storage.cache.PushStackCache;
import dev.smartshub.shkoth.storage.database.dao.PlayerStatsDAO;
import org.bukkit.Bukkit;
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
        PushStackCache.pushArg1(event.getKoth().getDisplayName());
        notifyService.sendBroadcastListToOnlinePlayers("koth.end");

        if(event.getReason() != KothEndEvent.EndReason.CAPTURE_COMPLETED) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                notifyService.sendTitle(player, "koth.end-by-timeout.title", "koth.end-by-timeout.subtitle");
                notifyService.playSound(player, "koth.end-by-timeout");
                notifyService.sendActionBar(player, "koth.end-by-timeout");

            });
            return;
        }

        Bukkit.getOnlinePlayers().forEach(player -> {
            notifyService.sendTitle(player, "koth.end.title", "koth.end.subtitle");
            notifyService.playSound(player, "koth.end");
            notifyService.sendActionBar(player, "koth.end");
        });
        event.getWinners().forEach(player -> {
            if(event.getKoth().isSolo()){
                playerStatsDAO.increaseSoloWin(player);
            } else {
                playerStatsDAO.increaseTeamWin(player);
            }
        });
    }

}
