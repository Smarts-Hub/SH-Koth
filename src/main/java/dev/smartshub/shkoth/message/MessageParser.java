package dev.smartshub.shkoth.message;

import dev.smartshub.shkoth.storage.cache.PushStackCache;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class MessageParser {

    private final MiniMessage miniMessage;
    private final boolean placeholderAPIAvailable;

    public MessageParser() {
        this.miniMessage = MiniMessage.builder()
                .postProcessor(component -> component.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                .build();
        this.placeholderAPIAvailable = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");

        if (!placeholderAPIAvailable) {
            Bukkit.getLogger().info("[SH-KoTH] PlaceholderAPI not available - placeholders will not be processed");
        }
    }

    public Component parseWithPlayer(String message, Player player) {
        if (message == null || message.trim().isEmpty()) {
            return Component.empty();
        }

        var replaced = message.replace("%shkoth_koth_context%", PushStackCache.getArg1() == null ? "" : PushStackCache.getArg1())
                .replace("%shkoth_player_context%", PushStackCache.getArg2() == null ? "" : PushStackCache.getArg2())
                .replace("%shkoth_aux_context%", PushStackCache.getArg3() == null ? "" : PushStackCache.getArg3());

        String processedMessage = placeholderAPIAvailable
                ? PlaceholderAPI.setPlaceholders(player, replaced)
                : replaced;
        return miniMessage.deserialize(processedMessage);
    }

    public Component parse(String message) {
        if (message == null || message.trim().isEmpty()) {
            return Component.empty();
        }

        var replaced = message.replace("%shkoth_koth_context%", PushStackCache.getArg1() == null ? "" : PushStackCache.getArg1())
                .replace("%shkoth_winner_context%", PushStackCache.getArg2() == null ? "" : PushStackCache.getArg2())
                .replace("%shkoth_aux_context%", PushStackCache.getArg3() == null ? "" : PushStackCache.getArg3());

        return miniMessage.deserialize(replaced);
    }

    public List<Component> parseListWithPlayer(List<String> messages, Player player) {
        if (messages == null || messages.isEmpty()) {
            return List.of();
        }

        return messages.stream()
                .filter(message -> message != null && !message.trim().isEmpty())
                .map(message -> parseWithPlayer(message, player))
                .collect(Collectors.toList());
    }

    public List<Component> parseList(List<String> messages) {
        if (messages == null || messages.isEmpty()) {
            return List.of();
        }

        return messages.stream()
                .filter(message -> message != null && !message.trim().isEmpty())
                .map(this::parse)
                .collect(Collectors.toList());
    }

    public String toString(Component component) {
        if (component == null) {
            return "";
        }
        return miniMessage.serialize(component);
    }
}
