package dev.smartshub.shkoth.service.gui.menu.other;

import dev.smartshub.shkoth.service.gui.menu.cache.KothToRegisterCache;
import dev.smartshub.shkoth.message.MessageParser;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KothLoreBoardPreview {

    private final KothToRegisterCache kothToRegisterCache;
    private final MessageParser parser;

    public KothLoreBoardPreview(KothToRegisterCache kothToRegisterCache, MessageParser parser) {
        this.kothToRegisterCache = kothToRegisterCache;
        this.parser = parser;
    }

    public List<Component> getCapturingLore(UUID uuid) {
        var data = kothToRegisterCache.getKothToRegister(uuid);

        List<Component> lore = new ArrayList<>();

        lore.add(parser.parse("<dark_gray>Right click: <gray>add line"));
        lore.add(parser.parse("<dark_gray>Left click: <gray>remove last line"));
        lore.add(parser.parse("<dark_gray>Shift click: <gray>remove all lines"));
        lore.add(Component.empty());
        lore.add(parser.parse("<yellow><bold>Preview:"));
        lore.add(Component.empty());

        String title = data.getScoreboardCapturingTitle();
        lore.add(parseForLore("Title: " + (title != null ? title : "Not set"), "<gold>"));

        lore.add(Component.empty());

        List<String> content = data.getScoreboardCapturingContent();
        if (content == null || content.isEmpty()) {
            lore.add(parser.parse("<dark_gray>No lines set"));
        } else {
            lore.add(parser.parse("<aqua>Lines (" + content.size() + "):"));
            for (int i = 0; i < content.size(); i++) {
                String line = content.get(i);
                lore.add(parseForLore((i + 1) + ". " + line, "<white>"));
            }
        }

        return lore;
    }

    public List<Component> getWaitingLore(UUID uuid) {
        var data = kothToRegisterCache.getKothToRegister(uuid);

        List<Component> lore = new ArrayList<>();

        lore.add(parser.parse("<dark_gray>Right click: <gray>add line"));
        lore.add(parser.parse("<dark_gray>Left click: <gray>remove last line"));
        lore.add(parser.parse("<dark_gray>Shift click: <gray>remove all lines"));
        lore.add(Component.empty());
        lore.add(parser.parse("<yellow><bold>Preview:"));
        lore.add(Component.empty());

        String title = data.getScoreboardWaitingTitle();
        lore.add(parseForLore("Title: " + (title != null ? title : "Not set"), "<gold>"));

        lore.add(Component.empty());

        List<String> content = data.getScoreboardWaitingContent();
        if (content == null || content.isEmpty()) {
            lore.add(parser.parse("<dark_gray>No lines set"));
        } else {
            lore.add(parser.parse("<aqua>Lines (" + content.size() + "):"));
            for (int i = 0; i < content.size(); i++) {
                String line = content.get(i);
                lore.add(parseForLore((i + 1) + ". " + line, "<white>"));
            }
        }

        return lore;
    }

    private Component parseForLore(String text, String defaultColor) {
        if (text == null || text.trim().isEmpty()) {
            return parser.parse("<gray>Not set");
        }

        try {
            return parser.parse(defaultColor + text);
        } catch (Exception e) {
            return parser.parse("<gray>" + text);
        }
    }
}