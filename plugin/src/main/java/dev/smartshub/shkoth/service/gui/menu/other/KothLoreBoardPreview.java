package dev.smartshub.shkoth.service.gui.menu.other;

import dev.smartshub.shkoth.message.MessageParser;
import dev.smartshub.shkoth.message.MessageRepository;
import dev.smartshub.shkoth.service.gui.menu.cache.KothToRegisterCache;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Scoreboard preview lore generator. Caches the rendered lore per player
 * UUID; call {@link #invalidate(UUID)} after any change to the underlying data.
 * All user-facing strings go through {@link MessageRepository}.
 */
public class KothLoreBoardPreview {

    private final KothToRegisterCache cache;
    private final MessageParser parser;
    private final MessageRepository msg;

    private final ConcurrentHashMap<UUID, List<Component>> capturingCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, List<Component>> waitingCache = new ConcurrentHashMap<>();

    public KothLoreBoardPreview(KothToRegisterCache cache, MessageParser parser, MessageRepository msg) {
        this.cache = cache;
        this.parser = parser;
        this.msg = msg;
    }

    public void invalidate(UUID uuid) {
        capturingCache.remove(uuid);
        waitingCache.remove(uuid);
    }

    public List<Component> getCapturingLore(UUID uuid) {
        return capturingCache.computeIfAbsent(uuid, this::buildCapturingLore);
    }

    public List<Component> getWaitingLore(UUID uuid) {
        return waitingCache.computeIfAbsent(uuid, this::buildWaitingLore);
    }

    private List<Component> buildCapturingLore(UUID uuid) {
        var data = cache.getKothToRegister(uuid);
        List<Component> lore = new ArrayList<>();
        appendHelpLines(lore);
        if (data == null) return lore;
        lore.add(titleLine(data.getScoreboardCapturingTitle()));
        lore.add(Component.empty());
        appendContentLines(lore, data.getScoreboardCapturingContent());
        return lore;
    }

    private List<Component> buildWaitingLore(UUID uuid) {
        var data = cache.getKothToRegister(uuid);
        List<Component> lore = new ArrayList<>();
        appendHelpLines(lore);
        if (data == null) return lore;
        lore.add(titleLine(data.getScoreboardWaitingTitle()));
        lore.add(Component.empty());
        appendContentLines(lore, data.getScoreboardWaitingContent());
        return lore;
    }

    private Component titleLine(String title) {
        String shown = (title == null || title.isEmpty())
                ? msg.getMessage("gui.common.not-set")
                : title;
        return parser.parse("<gold>" + msg.fmt("gui.board-preview.title-line", shown));
    }

    private void appendHelpLines(List<Component> lore) {
        lore.add(parser.parse(msg.getMessage("gui.board-preview.help-1")));
        lore.add(parser.parse(msg.getMessage("gui.board-preview.help-2")));
        lore.add(parser.parse(msg.getMessage("gui.board-preview.help-3")));
        lore.add(Component.empty());
        lore.add(parser.parse(msg.getMessage("gui.board-preview.preview-header")));
        lore.add(Component.empty());
    }

    private void appendContentLines(List<Component> lore, List<String> content) {
        if (content == null || content.isEmpty()) {
            lore.add(parser.parse(msg.getMessage("gui.board-preview.no-content")));
            return;
        }
        lore.add(parser.parse(msg.fmt("gui.board-preview.content-header", content.size())));
        for (int i = 0; i < content.size(); i++) {
            lore.add(parser.parse("<white>" + msg.fmt("gui.board-preview.content-line", i + 1, content.get(i))));
        }
    }
}
