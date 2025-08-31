package dev.smartshub.shkoth.listener.team;

import dev.smartshub.shkoth.api.event.team.MemberJoinedTeamEvent;
import dev.smartshub.shkoth.hook.placeholder.PlaceholderAPIHook;
import dev.smartshub.shkoth.service.notify.NotifyService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class MemberJoinedTeamListener implements Listener {

    private final NotifyService notifyService;

    public MemberJoinedTeamListener(NotifyService notifyService) {
        this.notifyService = notifyService;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMemberJoinTeam(MemberJoinedTeamEvent event){
        PlaceholderAPIHook.pushArgs("", event.getAddedPlayer().getName());
        notifyService.sendChat(event.getAddedPlayer(), "team.added-to-team");
        notifyService.sendTitle(event.getAddedPlayer(), "team.added-to-team.title", "team.added-to-team.subtitle");
        notifyService.sendActionBar(event.getAddedPlayer(), "team.added-to-team");
        notifyService.playSound(event.getAddedPlayer(), "team.added-to-team");
        for(Player player : event.getOldTeam().getOnlineMembers()){
            notifyService.sendChat(player, "team.new-member-joined");
            notifyService.sendTitle(player, "team.new-member-joined.title", "team.new-member-joined.subtitle");
            notifyService.sendActionBar(player, "team.new-member-joined");
            notifyService.playSound(player, "team.new-member-joined");
        }
    }

}
