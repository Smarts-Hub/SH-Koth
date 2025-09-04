package dev.smartshub.shkoth.listener.team;

import dev.smartshub.shkoth.api.event.team.TeamChangeLeaderEvent;
import dev.smartshub.shkoth.hook.placeholder.PlaceholderAPIHook;
import dev.smartshub.shkoth.service.notify.NotifyService;
import dev.smartshub.shkoth.storage.cache.PushStackCache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class TeamChangeLeaderListener implements Listener {

    private final NotifyService notifyService;

    public TeamChangeLeaderListener(NotifyService notifyService) {
        this.notifyService = notifyService;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTeamChangeLeader(TeamChangeLeaderEvent event){
        PushStackCache.pushArgs("", event.getNewLeaderPlayer().getName());
        for(Player player : event.getTeam().getOnlineMembers()){
            notifyService.sendChat(player, "team.change-leader");
            notifyService.sendTitle(player, "team.new-leader.title", "team.new-leader.subtitle");
            notifyService.sendActionBar(player, "team.new-leader");
            notifyService.playSound(player, "team.new-leader");
        }
        notifyService.sendChat(event.getOldLeaderPlayer(), "team.transferred-leadership-success");
        notifyService.sendChat(event.getNewLeaderPlayer(), "team.you-are-leader-now");
    }

}
