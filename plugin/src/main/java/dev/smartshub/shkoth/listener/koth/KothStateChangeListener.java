package dev.smartshub.shkoth.listener.koth;

import dev.smartshub.shkoth.api.event.koth.KothStateChangeEvent;
import dev.smartshub.shkoth.hook.discord.DiscordWebHookSender;
import dev.smartshub.shkoth.service.bossbar.AdventureBossbarService;
import dev.smartshub.shkoth.service.scoreboard.ScoreboardHandleService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class KothStateChangeListener implements Listener {

    private final ScoreboardHandleService scoreboardHandleService;
    private final AdventureBossbarService adventureBossbarService;
    private final DiscordWebHookSender discordWebHookSender;

    public KothStateChangeListener(ScoreboardHandleService scoreboardHandleService,
                                   AdventureBossbarService adventureBossbarService,
                                   DiscordWebHookSender discordWebHookSender) {
        this.scoreboardHandleService = scoreboardHandleService;
        this.adventureBossbarService = adventureBossbarService;
        this.discordWebHookSender = discordWebHookSender;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKothStateChange(KothStateChangeEvent event) {
        if(event.isCancelled()) return;

        discordWebHookSender.send(event);

        if(event.getKoth().isScoreboardEnabled()) {
            scoreboardHandleService.handleChange(event.getKoth(), event.getNewState());
        }

        adventureBossbarService.handleStateChange(event.getKoth(), event.getNewState());
    }
}
