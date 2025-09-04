package dev.smartshub.shkoth.listener.team;

import dev.smartshub.shkoth.api.event.team.MemberLeavedTeamEvent;
import dev.smartshub.shkoth.hook.placeholder.PlaceholderAPIHook;
import dev.smartshub.shkoth.service.notify.NotifyService;
import dev.smartshub.shkoth.storage.cache.PushStackCache;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class MemberLeavedTeamListener implements Listener {

    private final NotifyService notifyService;

    public MemberLeavedTeamListener(NotifyService notifyService) {
        this.notifyService = notifyService;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMemberLeaveTeam(MemberLeavedTeamEvent event){
        PushStackCache.pushArgs("", event.getRemovedPlayer().getName());
        notifyService.sendChat(event.getRemovedPlayer(), "team.removed-from-team");
        notifyService.sendTitle(event.getRemovedPlayer(), "team.removed.title", "team.removed.subtitle");
        notifyService.sendActionBar(event.getRemovedPlayer(), "team.removed");
        notifyService.playSound(event.getRemovedPlayer(), "team.removed");
        for (var player : event.getNewTeam().getOnlineMembers()) {
            notifyService.sendChat(player, "team.member-removed");
            notifyService.sendTitle(player, "team.member-removed.title", "team.member-removed.subtitle");
            notifyService.sendActionBar(player, "team.member-removed");
            notifyService.playSound(player, "team.member-removed");
        }

    }

}
