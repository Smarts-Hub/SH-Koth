package dev.smartshub.shkoth.service.team;

import dev.smartshub.shkoth.api.team.Team;
import dev.smartshub.shkoth.team.tracker.GlobalTeamTracker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamChatService {

    private final GlobalTeamTracker tracker = GlobalTeamTracker.getInstance();

    public void sendTeamMessage(Player sender, String message) {
        Team team = tracker.getTeamFrom(sender.getUniqueId());
        if (team == null) {
            sender.sendMessage("§cYou are not in a team.");
            return;
        }

        String formatted = "§a[Team] " + sender.getName() + ": §f" + message;
        for (UUID memberId : team.members()) {
            Player member = Bukkit.getPlayer(memberId);
            if (member != null && member.isOnline()) {
                member.sendMessage(formatted);
            }
        }
    }
}
