package dev.smartshub.shkoth.service.notify;

import dev.smartshub.shkoth.message.MessageParser;
import dev.smartshub.shkoth.message.MessageRepository;
import me.lucko.spark.paper.common.command.sender.CommandSender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Collection;
import java.util.List;

public class NotifyService {

    private final MessageParser parser;
    private final MessageRepository repository;

    // Default durations for titles
    private final Duration fadeIn = Duration.ofMillis(500);
    private final Duration stay = Duration.ofSeconds(3);
    private final Duration fadeOut = Duration.ofMillis(500);

    public NotifyService(MessageParser parser, MessageRepository repository) {
        this.parser = parser;
        this.repository = repository;
    }

    public void sendChat(Player player, String path) {
        if (player == null) return;
        String message = repository.getMessage(path);
        if (message == null || message.trim().isEmpty()) return;

        Component component = parser.parseWithPlayer(message, player);
        player.sendMessage(component);
    }

    public void sendChat(CommandSender sender, String path) {
        if (sender == null) return;
        String message = repository.getMessage(path);
        if (message == null || message.trim().isEmpty()) return;

        Component component = parser.parse(message);
        sender.sendMessage(component);
    }

    public void sendRawMessage(Player player, String message) {
        player.sendMessage(message);
    }

    public void sendTitle(Player player, String path) {
        if (player == null) return;
        String titleText = repository.getMessage(path);
        if (titleText == null || titleText.trim().isEmpty()) return;

        Component titleComponent = parser.parseWithPlayer(titleText, player);
        Title title = Title.title(titleComponent, Component.empty(),
                Title.Times.times(fadeIn, stay, fadeOut));
        player.showTitle(title);
    }

    public void sendSubtitle(Player player, String path) {
        if (player == null) return;
        String subtitleText = repository.getMessage(path);
        if (subtitleText == null || subtitleText.trim().isEmpty()) return;

        Component subtitleComponent = parser.parseWithPlayer(subtitleText, player);
        Title title = Title.title(Component.empty(), subtitleComponent,
                Title.Times.times(fadeIn, stay, fadeOut));
        player.showTitle(title);
    }

    public void sendTitle(Player player, String titlePath, String subtitlePath) {
        if (player == null) return;

        String titleText = repository.getMessage(titlePath);
        String subtitleText = repository.getMessage(subtitlePath);

        Component titleComp = (titleText != null && !titleText.trim().isEmpty())
                ? parser.parseWithPlayer(titleText, player)
                : Component.empty();

        Component subtitleComp = (subtitleText != null && !subtitleText.trim().isEmpty())
                ? parser.parseWithPlayer(subtitleText, player)
                : Component.empty();

        Title title = Title.title(titleComp, subtitleComp,
                Title.Times.times(fadeIn, stay, fadeOut));
        player.showTitle(title);
    }

    public void sendTitle(Player player, String titlePath, String subtitlePath,
                          Duration fadeIn, Duration stay, Duration fadeOut) {
        if (player == null) return;

        String titleText = repository.getMessage(titlePath);
        String subtitleText = repository.getMessage(subtitlePath);

        Component titleComp = (titleText != null && !titleText.trim().isEmpty())
                ? parser.parseWithPlayer(titleText, player)
                : Component.empty();

        Component subtitleComp = (subtitleText != null && !subtitleText.trim().isEmpty())
                ? parser.parseWithPlayer(subtitleText, player)
                : Component.empty();

        Title title = Title.title(titleComp, subtitleComp,
                Title.Times.times(fadeIn, stay, fadeOut));
        player.showTitle(title);
    }

    public void sendActionBar(Player player, String path) {
        if (player == null) return;
        String message = repository.getMessage(path);
        if (message == null || message.trim().isEmpty()) return;

        Component component = parser.parseWithPlayer(message, player);
        player.sendActionBar(component);
    }

    public void sendMultipleLines(Player player, String path) {
        if (player == null) return;
        List<String> lines = repository.getBroadcastMessageList(path);
        if (lines == null || lines.isEmpty()) return;

        List<Component> components = parser.parseListWithPlayer(lines, player);
        components.forEach(player::sendMessage);
    }

    public void sendMultipleLines(CommandSender sender, String path) {
        if (sender == null) return;
        List<String> lines = repository.getBroadcastMessageList(path);
        if (lines == null || lines.isEmpty()) return;

        List<Component> components = parser.parseList(lines);
        components.forEach(sender::sendMessage);
    }

    public void sendBroadcast(String path, Collection<Player> players) {
        if (players == null || players.isEmpty()) return;
        String message = repository.getBroadcastMessage(path);
        if (message == null || message.trim().isEmpty()) return;

        Component component = parser.parse(message);
        for (Player player : players) {
            if (player == null || !player.isOnline()) continue;
            player.sendMessage(component);
        }
    }

    public void sendBroadcastList(String path, Collection<Player> players) {
        if (players == null || players.isEmpty()) return;
        List<String> messages = repository.getBroadcastMessageList(path);
        if (messages == null || messages.isEmpty()) return;

        List<Component> components = parser.parseList(messages);
        for (Player player : players) {
            if (player == null || !player.isOnline()) continue;
            components.forEach(player::sendMessage);
        }
    }

    public void sendBroadcastToOnlinePlayers(String path) {
        String message = repository.getBroadcastMessage(path);
        if (message == null || message.trim().isEmpty()) return;

        Component component = parser.parse(message);
        for (Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            player.sendMessage(component);
        }
    }

    public void sendBroadcastListToOnlinePlayers(String path) {
        List<String> messages = repository.getBroadcastMessageList(path);
        if (messages == null || messages.isEmpty()) return;

        List<Component> components = parser.parseList(messages);
        for (Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            components.forEach(player::sendMessage);
        }
    }
}
