package dev.smartshub.shkoth.service.notify;

import dev.smartshub.shkoth.message.MessageParser;
import dev.smartshub.shkoth.message.MessageRepository;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
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

    public void sendTeamMessage(Player player, String message) {
        var finalMessage = parser.parse(repository.getMessage("team.chat-prefix"))
                .append(Component.text(message));
        player.sendMessage(finalMessage);
    }

    public void sendRawMessage(CommandSender sender, String message) {
        sender.sendMessage(message);
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

        String titleText = repository.getTitle(titlePath);
        String subtitleText = repository.getSubtittle(subtitlePath);

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
        String message = repository.getActionbar(path);
        if (message == null || message.trim().isEmpty()) return;

        Component component = parser.parseWithPlayer(message, player);
        player.sendActionBar(component);
    }

    public void playSound(Player player, String path) {
        if (player == null) return;
        String soundString = repository.getSound(path);
        if (soundString == null || soundString.trim().isEmpty()) return;

        try {
            Sound sound = Sound.sound(Key.key(soundString), Sound.Source.PLAYER, 1.0f, 1.0f);
            player.playSound(sound);
        } catch (IllegalArgumentException e) {
            // Invalid sound, do nothing
        }
    }

    public void sendBroadcastListToOnlinePlayers(String path) {
        List<String> messages = repository.getBroadcastMessageList(path);
        if (messages == null || messages.isEmpty()) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            List<Component> components = parser.parseListWithPlayer(messages, player);
            components.forEach(player::sendMessage);
        }
    }

}

