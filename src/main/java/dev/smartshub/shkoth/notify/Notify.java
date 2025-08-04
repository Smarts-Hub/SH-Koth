package dev.smartshub.shkoth.notify;

import dev.smartshub.shkoth.storage.config.FileManager;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Notify {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public static Component parse(String msg, Player player) {
        String withPlaceholders = PlaceholderAPI.setPlaceholders(player, msg);
        return miniMessage.deserialize(withPlaceholders);
    }

    public static Component parse(String msg) {
        return miniMessage.deserialize(msg);
    }

    public static List<Component> parseList(List<String> lore, Player player) {
        List<Component> components = new ArrayList<>();
        for (String line : lore) {
            components.add(parse(line, player));
        }
        return components;
    }

    public static void send(Player player, String path) {
        String msg = getMsg(path);
        if(msg.equalsIgnoreCase("")) return;
        player.sendMessage(parse(msg, player));
    }

    public static void send(CommandSender sender, String path) {
        String msg = getMsg(path);
        sender.sendMessage(parse(msg));
    }

    public static void sendParsed(Player player, String msg) {
        player.sendMessage(parse(msg, player));
    }

    public static void sendTitle(Player player, String title) {
        player.showTitle(Title.title(parse(title, player), Component.empty()));
    }

    public static void sendSubtitle(Player player, String subtitle) {
        player.showTitle(Title.title(Component.empty(), parse(subtitle, player)));
    }

    public static void sendActionbar(Player player, String bar) {
        player.sendActionBar(parse(bar, player));
    }

    public static void broadcast(String path) {
        String fullPath = "broadcast." + path;
        var config = FileManager.get("messages");

        if (config.isList(fullPath)) {
            List<String> lines = config.getStringList(fullPath);
            for (Player player : Bukkit.getOnlinePlayers()) {
                for (Component line : parseList(lines, player)) {
                    player.sendMessage(line);
                }
            }
        } else {
            String msg = config.getString(fullPath);
            if (msg == null) {
                msg = "<red>Message not found: " + fullPath;
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(parse(msg, player));
            }
        }
    }

    private static String getMsg(String path) {
        String fullPath = "messages." + path;
        String msg = FileManager.get("messages").getString(fullPath);
        return msg != null ? msg : "<red>Message not found: " + fullPath;
    }
}
