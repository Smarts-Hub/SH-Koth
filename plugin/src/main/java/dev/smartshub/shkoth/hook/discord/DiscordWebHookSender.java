package dev.smartshub.shkoth.hook.discord;

import dev.smartshub.shkoth.api.config.ConfigContainer;
import dev.smartshub.shkoth.api.event.key.DiscordKey;
import io.github._4drian3d.jdwebhooks.Embed;
import io.github._4drian3d.jdwebhooks.WebHook;
import io.github._4drian3d.jdwebhooks.WebHookClient;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;

import java.time.Instant;

public class DiscordWebHookSender {

    private final ConfigContainer config;
    private final boolean placeholderAPIAvailable;

    public DiscordWebHookSender(ConfigContainer config) {
        this.config = config;
        this.placeholderAPIAvailable = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    public void send(DiscordKey event) {
        final String key = "koth-" + event.getDiscordKey();

        final boolean enabled = config.getBoolean(key + ".enabled", false);
        if(!enabled) return;

        final String url = config.getString("url", "");
        if(url.isEmpty()) return;

        var username = parsePlaceholders(config.getString(key + ".username", "SH-Koth"));
        var author = parsePlaceholders(config.getString(key + ".author", "SH-Koth"));
        var timestamp = config.getBoolean(key + ".timestamp", true);
        var title = parsePlaceholders(config.getString(key + ".title", ""));
        var description = parsePlaceholders(config.getString(key + ".description", ""));
        var footer = parsePlaceholders(config.getString(key + ".footer", ""));
        var color = config.getInt(key + ".color", 0x3498db);

        final WebHookClient client = WebHookClient.fromURL(url);
        var embedBuilder = Embed.builder()
                .author(Embed.Author.builder().name(author).build())
                .color(color)
                .title(title)
                .description(description)
                .footer(Embed.Footer.builder().text(footer).build());

        if(timestamp) {
            embedBuilder.timestamp(Instant.now());
        }

        final var embed = embedBuilder.build();


        final WebHook webHook = WebHook.builder()
                .username(username)
                .embed(embed)
                .build();

        client.sendWebHook(webHook);

    }

    private String parsePlaceholders(String message) {
        return placeholderAPIAvailable
                ? PlaceholderAPI.setPlaceholders(null, message)
                : message;
    }

}
