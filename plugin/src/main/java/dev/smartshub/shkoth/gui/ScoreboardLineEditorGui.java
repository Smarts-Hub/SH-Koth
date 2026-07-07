package dev.smartshub.shkoth.gui;

import dev.smartshub.shkoth.message.MessageParser;
import dev.smartshub.shkoth.message.MessageRepository;
import dev.smartshub.shkoth.service.gui.GuiService;
import dev.smartshub.shkoth.service.gui.input.AnvilTextInput;
import dev.smartshub.shkoth.service.gui.menu.cache.KothTempData;
import dev.smartshub.shkoth.service.gui.menu.cache.KothToRegisterCache;
import dev.smartshub.shkoth.service.gui.menu.other.KothLoreBoardPreview;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Per-line editor for the capturing / waiting scoreboard line lists.
 * 6-row paginated view: header (back / info / add / clear),
 * 4 rows of line entries (number, text x5, up, down, delete), footer (prev / page / next).
 * Add and edit both go through {@link AnvilTextInput}.
 */
public class ScoreboardLineEditorGui {

    private static final int LINES_PER_PAGE = 4;

    private static final int SLOT_BACK = 0;
    private static final int SLOT_TITLE = 4;
    private static final int SLOT_ADD = 7;
    private static final int SLOT_CLEAR = 8;

    private static final int CONTENT_ROW_START = 1;

    private static final int SLOT_PREV = 45;
    private static final int SLOT_PAGE_INFO = 49;
    private static final int SLOT_NEXT = 53;

    private final MessageParser parser;
    private final MessageRepository msg;
    private final KothToRegisterCache cache;
    private final KothLoreBoardPreview boardPreview;

    private GuiService guiService;

    private final Map<UUID, Boolean> editingCapturing = new HashMap<>();
    private final Map<UUID, Integer> currentPage = new HashMap<>();

    public ScoreboardLineEditorGui(MessageParser parser, MessageRepository msg,
                                   KothToRegisterCache cache, KothLoreBoardPreview boardPreview) {
        this.parser = parser;
        this.msg = msg;
        this.cache = cache;
        this.boardPreview = boardPreview;
    }

    public void setGuiService(GuiService guiService) {
        this.guiService = guiService;
    }

    public void open(Player player, boolean capturing) {
        editingCapturing.put(player.getUniqueId(), capturing);
        currentPage.putIfAbsent(player.getUniqueId(), 0);
        render(player);
    }

    private void render(Player player) {
        boolean capturing = editingCapturing.getOrDefault(player.getUniqueId(), false);
        KothTempData data = cache.getKothToRegister(player.getUniqueId());
        if (data == null) {
            player.closeInventory();
            return;
        }
        List<String> lines = data.getLines(capturing);
        int page = clampPage(player, lines.size());

        Gui gui = Gui.gui()
                .title(parser.parse(msg.getMessage(capturing
                        ? "gui.scoreboard-editor.title-capturing"
                        : "gui.scoreboard-editor.title-waiting")))
                .rows(6)
                .create();

        gui.setItem(SLOT_BACK, ItemBuilder.from(Material.ARROW)
                .name(parser.parse(msg.getMessage("gui.scoreboard-editor.back-name")))
                .lore(parser.parse(msg.getMessage("gui.scoreboard-editor.back-lore")))
                .asGuiItem(e -> {
                    e.setCancelled(true);
                    boardPreview.invalidate(player.getUniqueId());
                    if (guiService != null) guiService.openCreateKothGui(player);
                }));

        gui.setItem(SLOT_TITLE, ItemBuilder.from(Material.PAPER)
                .name(parser.parse(msg.getMessage(capturing
                        ? "gui.scoreboard-editor.info-name-capturing"
                        : "gui.scoreboard-editor.info-name-waiting")))
                .lore(parser.parseList(List.of(
                        msg.fmt("gui.scoreboard-editor.info-lore-1", lines.size()),
                        msg.getMessage("gui.scoreboard-editor.info-lore-2"),
                        msg.getMessage("gui.scoreboard-editor.info-lore-3"))))
                .asGuiItem(e -> e.setCancelled(true)));

        gui.setItem(SLOT_ADD, ItemBuilder.from(Material.EMERALD)
                .name(parser.parse(msg.getMessage("gui.scoreboard-editor.add-name")))
                .lore(parser.parse(msg.getMessage("gui.scoreboard-editor.add-lore")))
                .asGuiItem(e -> {
                    e.setCancelled(true);
                    openAnvilForNewLine(player, capturing);
                }));

        gui.setItem(SLOT_CLEAR, ItemBuilder.from(Material.BARRIER)
                .name(parser.parse(msg.getMessage("gui.scoreboard-editor.clear-name")))
                .lore(parser.parse(msg.getMessage("gui.scoreboard-editor.clear-lore")))
                .asGuiItem(e -> {
                    e.setCancelled(true);
                    if (capturing) data.clearCapturingLines();
                    else data.clearWaitingLines();
                    boardPreview.invalidate(player.getUniqueId());
                    render(player);
                }));

        int firstIdx = page * LINES_PER_PAGE;
        for (int row = 0; row < LINES_PER_PAGE; row++) {
            int idx = firstIdx + row;
            int baseSlot = (CONTENT_ROW_START + row) * 9;
            if (idx >= lines.size()) {
                fillEmpty(gui, baseSlot);
            } else {
                placeLineRow(gui, player, capturing, lines.get(idx), idx, baseSlot);
            }
        }

        int totalPages = Math.max(1, (lines.size() + LINES_PER_PAGE - 1) / LINES_PER_PAGE);
        gui.setItem(SLOT_PREV, ItemBuilder.from(page > 0 ? Material.SPECTRAL_ARROW : Material.GRAY_DYE)
                .name(parser.parse(msg.getMessage(page > 0
                        ? "gui.scoreboard-editor.prev-page-on"
                        : "gui.scoreboard-editor.prev-page-off")))
                .asGuiItem(e -> {
                    e.setCancelled(true);
                    if (page > 0) {
                        currentPage.put(player.getUniqueId(), page - 1);
                        render(player);
                    }
                }));

        gui.setItem(SLOT_PAGE_INFO, ItemBuilder.from(Material.BOOK)
                .name(parser.parse(msg.fmt("gui.scoreboard-editor.page-info", page + 1, totalPages)))
                .asGuiItem(e -> e.setCancelled(true)));

        gui.setItem(SLOT_NEXT, ItemBuilder.from(page < totalPages - 1 ? Material.SPECTRAL_ARROW : Material.GRAY_DYE)
                .name(parser.parse(msg.getMessage(page < totalPages - 1
                        ? "gui.scoreboard-editor.next-page-on"
                        : "gui.scoreboard-editor.next-page-off")))
                .asGuiItem(e -> {
                    e.setCancelled(true);
                    if (page < totalPages - 1) {
                        currentPage.put(player.getUniqueId(), page + 1);
                        render(player);
                    }
                }));

        GuiItem filler = ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE)
                .name(parser.parse(" "))
                .asGuiItem(e -> e.setCancelled(true));
        for (int s = 45; s < 54; s++) if (gui.getGuiItem(s) == null) gui.setItem(s, filler);
        for (int s = 0; s < 9; s++) if (gui.getGuiItem(s) == null) gui.setItem(s, filler);

        gui.open(player);
    }

    private void placeLineRow(Gui gui, Player player, boolean capturing, String text, int idx, int baseSlot) {
        gui.setItem(baseSlot, ItemBuilder.from(Material.NAME_TAG)
                .name(parser.parse(msg.fmt("gui.scoreboard-editor.line-number", idx + 1)))
                .asGuiItem(e -> e.setCancelled(true)));

        String displayText = (text == null || text.isEmpty())
                ? msg.getMessage("gui.scoreboard-editor.line-empty")
                : text;

        GuiItem textItem = ItemBuilder.from(Material.PAPER)
                .name(parser.parse(msg.fmt("gui.scoreboard-editor.line-text", displayText)))
                .lore(parser.parseList(List.of(
                        msg.getMessage("gui.scoreboard-editor.line-action-1"),
                        msg.getMessage("gui.scoreboard-editor.line-action-2"))))
                .asGuiItem(e -> {
                    e.setCancelled(true);
                    if (e.getClick().isShiftClick()) {
                        if (e.getClick().isLeftClick()) moveUp(player, capturing, idx);
                        else if (e.getClick().isRightClick()) moveDown(player, capturing, idx);
                    } else if (e.getClick().isLeftClick()) {
                        openAnvilForEdit(player, capturing, idx, text);
                    } else if (e.getClick().isRightClick()) {
                        deleteLine(player, capturing, idx);
                    }
                });
        for (int i = 1; i <= 5; i++) gui.setItem(baseSlot + i, textItem);

        gui.setItem(baseSlot + 6, ItemBuilder.from(Material.ARROW)
                .name(parser.parse(msg.getMessage("gui.scoreboard-editor.move-up")))
                .asGuiItem(e -> {
                    e.setCancelled(true);
                    moveUp(player, capturing, idx);
                }));

        gui.setItem(baseSlot + 7, ItemBuilder.from(Material.ARROW)
                .name(parser.parse(msg.getMessage("gui.scoreboard-editor.move-down")))
                .asGuiItem(e -> {
                    e.setCancelled(true);
                    moveDown(player, capturing, idx);
                }));

        gui.setItem(baseSlot + 8, ItemBuilder.from(Material.LAVA_BUCKET)
                .name(parser.parse(msg.getMessage("gui.scoreboard-editor.delete")))
                .asGuiItem(e -> {
                    e.setCancelled(true);
                    deleteLine(player, capturing, idx);
                }));
    }

    private void fillEmpty(Gui gui, int baseSlot) {
        GuiItem filler = ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE)
                .name(parser.parse(" "))
                .asGuiItem(e -> e.setCancelled(true));
        for (int i = 0; i < 9; i++) gui.setItem(baseSlot + i, filler);
    }

    private int clampPage(Player player, int totalLines) {
        int totalPages = Math.max(1, (totalLines + LINES_PER_PAGE - 1) / LINES_PER_PAGE);
        int p = currentPage.getOrDefault(player.getUniqueId(), 0);
        if (p >= totalPages) p = totalPages - 1;
        if (p < 0) p = 0;
        currentPage.put(player.getUniqueId(), p);
        return p;
    }

    private void openAnvilForNewLine(Player player, boolean capturing) {
        AnvilTextInput anvil = AnvilTextInput.get();
        if (anvil == null) return;
        anvil.prompt(player, "", text -> {
            if (text == null || text.isEmpty()) {
                open(player, capturing);
                return;
            }
            KothTempData data = cache.getKothToRegister(player.getUniqueId());
            if (data == null) return;
            if (capturing) data.addCapturingLine(text);
            else data.addWaitingLine(text);
            boardPreview.invalidate(player.getUniqueId());
            open(player, capturing);
        }, () -> open(player, capturing));
    }

    private void openAnvilForEdit(Player player, boolean capturing, int idx, String current) {
        AnvilTextInput anvil = AnvilTextInput.get();
        if (anvil == null) return;
        anvil.prompt(player, current == null ? "" : current, text -> {
            KothTempData data = cache.getKothToRegister(player.getUniqueId());
            if (data == null) return;
            data.setLineAt(capturing, idx, text);
            boardPreview.invalidate(player.getUniqueId());
            open(player, capturing);
        }, () -> open(player, capturing));
    }

    private void deleteLine(Player player, boolean capturing, int idx) {
        KothTempData data = cache.getKothToRegister(player.getUniqueId());
        if (data == null) return;
        data.removeLineAt(capturing, idx);
        boardPreview.invalidate(player.getUniqueId());
        render(player);
    }

    private void moveUp(Player player, boolean capturing, int idx) {
        if (idx <= 0) return;
        KothTempData data = cache.getKothToRegister(player.getUniqueId());
        if (data == null) return;
        data.moveLine(capturing, idx, idx - 1);
        boardPreview.invalidate(player.getUniqueId());
        render(player);
    }

    private void moveDown(Player player, boolean capturing, int idx) {
        KothTempData data = cache.getKothToRegister(player.getUniqueId());
        if (data == null) return;
        List<String> lines = data.getLines(capturing);
        if (idx >= lines.size() - 1) return;
        data.moveLine(capturing, idx, idx + 1);
        boardPreview.invalidate(player.getUniqueId());
        render(player);
    }
}
