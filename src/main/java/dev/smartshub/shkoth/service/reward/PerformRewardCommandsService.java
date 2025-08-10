package dev.smartshub.shkoth.service.reward;

import dev.smartshub.shkoth.api.koth.Koth;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PerformRewardCommandsService {

    private final Koth koth;
    private final boolean isPlaceholderAPIAvailable;

    public PerformRewardCommandsService(Koth koth) {
        this.koth = koth;
        this.isPlaceholderAPIAvailable = isPlaceholderAPIRegistered();
    }

    public void performCommands() {
        Set<UUID> winners = koth.getWinners();
        List<String> commands = koth.getCommands().winnersCommands();
        if (commands.isEmpty() || winners.isEmpty()) {
            return;
        }

        List<Player> onlinePlayers = getOnlineWinners();

        if (onlinePlayers.isEmpty()) {
            return;
        }

        for (String command : commands) {
            for (Player player : onlinePlayers) {
                String parsedCommand = parseCommand(player, command);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsedCommand);
            }
        }
    }

    private List<Player> getOnlineWinners() {
        Set<UUID> winners = koth.getWinners();
        return winners.stream()
                .map(Bukkit::getPlayer)
                .filter(player -> player != null && player.isOnline())
                .toList();
    }

    private String parseCommand(Player player, String command) {
        return isPlaceholderAPIAvailable
                ? PlaceholderAPI.setPlaceholders(player, command)
                : command;
    }

    private static boolean isPlaceholderAPIRegistered() {
        try {
            return PlaceholderAPI.isRegistered("shkoth");
        } catch (Exception e) {
            return false;
        }
    }
}
