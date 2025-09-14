package dev.smartshub.shkoth.listener.team;

import dev.smartshub.shkoth.api.event.team.MemberKickedFromTeamEvent;
import dev.smartshub.shkoth.hook.placeholder.PlaceholderAPIHook;
import dev.smartshub.shkoth.service.notify.NotifyService;
import dev.smartshub.shkoth.storage.cache.PushStackCache;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class MemberKickedFromTeamListener implements Listener {

    private final NotifyService notifyService;

    public MemberKickedFromTeamListener(NotifyService notifyService) {
        this.notifyService = notifyService;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMemberKickedFromTeam(MemberKickedFromTeamEvent event) {
        PushStackCache.pushArg2(event.getKickedPlayer().getName());
        notifyService.sendChat(event.getKickedPlayer(), "team.kicked-from-team");
        notifyService.sendTitle(event.getKickedPlayer(), "team.kicked.title", "team.kicked.subtitle");
        notifyService.sendActionBar(event.getKickedPlayer(), "team.kicked");
        notifyService.playSound(event.getKickedPlayer(), "team.kicked");
        for (var player : event.getOldTeam().getOnlineMembers()) {
            notifyService.sendChat(player, "team.member-kicked");
            notifyService.sendTitle(player, "team.member-kicked.title", "team.member-kicked.subtitle");
            notifyService.sendActionBar(player, "team.member-kicked");
            notifyService.playSound(player, "team.member-kicked");
        }
        notifyService.sendChat(event.getKickerPlayer(), "team.kick-member-success");
    }
}
