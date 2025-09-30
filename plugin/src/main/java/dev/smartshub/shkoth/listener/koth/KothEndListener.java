package dev.smartshub.shkoth.listener.koth;

import dev.smartshub.shkoth.api.event.koth.KothEndEvent;
import dev.smartshub.shkoth.hook.discord.DiscordWebHookSender;
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
    private final DiscordWebHookSender discordWebHookSender;

    public KothEndListener(NotifyService notifyService, PlayerStatsDAO playerStatsDAO,
                           DiscordWebHookSender discordWebHookSender) {
        this.notifyService = notifyService;
        this.playerStatsDAO = playerStatsDAO;
        this.discordWebHookSender = discordWebHookSender;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKothEnd(KothEndEvent event) {
        PushStackCache.pushArg1(event.getKoth().getDisplayName());
        notifyService.sendBroadcastListToOnlinePlayers("koth.end");
        discordWebHookSender.send(event);

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

        PushStackCache.pushArg4(event.getKoth().getTeamTracker().getTeamDisplayName(event.getWinnerPlayers().getFirst().getUniqueId()));
    }

}
