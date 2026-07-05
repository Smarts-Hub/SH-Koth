package dev.smartshub.shkoth.gui;

import dev.smartshub.shkoth.api.gui.BaseUpdatableGui;
import dev.smartshub.shkoth.api.koth.guideline.KothType;
import dev.smartshub.shkoth.api.koth.guideline.NotifyType;
import dev.smartshub.shkoth.message.MessageParser;
import dev.smartshub.shkoth.message.MessageRepository;
import dev.smartshub.shkoth.registry.KothRegistry;
import dev.smartshub.shkoth.service.gui.GuiService;
import dev.smartshub.shkoth.service.gui.input.AnvilTextInput;
import dev.smartshub.shkoth.service.gui.menu.cache.KothValidation;
import dev.smartshub.shkoth.service.gui.menu.other.KothLoreBoardPreview;
import dev.smartshub.shkoth.service.wand.WandService;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Tabbed KOTH creation/editing GUI.
 * 6 rows × 9 columns = 54 slots:
 *   row 0  (0-8)   : tab bar + close
 *   row 1-4 (9-44) : content of the active tab
 *   row 5  (45-53) : footer actions (test / copy / validation / save)
 *
 * Edit-mode detection: if the temp data's id is already in the KothRegistry,
 * we treat the session as editing an existing KOTH.
 *
 * All user-facing strings are looked up through {@link MessageRepository}.
 */
public class CreateKothGui extends BaseUpdatableGui {

    private static final int SLOT_TAB_BASIC = 1;
    private static final int SLOT_TAB_TIME = 3;
    private static final int SLOT_TAB_REWARD = 5;
    private static final int SLOT_CLOSE = 8;

    private static final int SLOT_B_ID = 20;
    private static final int SLOT_B_NAME = 22;
    private static final int SLOT_B_WAND = 24;
    private static final int SLOT_B_TYPE = 30;
    private static final int SLOT_B_NOTIFY = 32;
    // Team settings (row 4); only shown when the internal team command is enabled.
    private static final int SLOT_B_SOLO = 37;
    private static final int SLOT_B_DENY = 39;
    private static final int SLOT_B_CREATE = 41;

    private static final int SLOT_T_MAX = 19;
    private static final int SLOT_T_CAP = 21;
    private static final int SLOT_T_BOSS = 23;
    private static final int SLOT_T_SCB_TOGGLE = 25;
    private static final int SLOT_T_WAIT_TITLE = 28;
    private static final int SLOT_T_WAIT_LINES = 30;
    private static final int SLOT_T_CAP_TITLE = 32;
    private static final int SLOT_T_CAP_LINES = 34;

    private static final int SLOT_R_REWARDS = 20;
    private static final int SLOT_R_POOL_INFO = 22;
    private static final int SLOT_R_COMMANDS = 24;

    private static final int SLOT_F_TEST = 47;
    private static final int SLOT_F_COPY = 49;
    private static final int SLOT_F_VALIDATION = 51;
    private static final int SLOT_F_SAVE = 53;

    private static final int CONTENT_FIRST = 9;
    private static final int CONTENT_LAST = 44;
    private static final int FOOTER_FIRST = 45;
    private static final int FOOTER_LAST = 53;

    private static final int TAB_BASIC = 0;
    private static final int TAB_TIME = 1;
    private static final int TAB_REWARD = 2;

    private final MessageParser parser;
    private final MessageRepository msg;
    private final dev.smartshub.shkoth.service.gui.menu.cache.KothToRegisterCache cache;
    private final KothLoreBoardPreview boardPreview;
    private final WandService wandService;
    private final KothRegistry kothRegistry;

    private GuiService guiService;
    // Mirrors internal-team.disable-command: when the built-in team command is
    // disabled, the team-mode editor toggles are hidden too. Defaults to visible.
    private boolean teamTogglesVisible = true;
    private final Map<UUID, Integer> currentTab = new HashMap<>();
    private final Map<UUID, Gui> openGuis = new HashMap<>();

    public CreateKothGui(MessageParser parser,
                         MessageRepository msg,
                         dev.smartshub.shkoth.service.gui.menu.cache.KothToRegisterCache cache,
                         WandService wandService,
                         KothRegistry kothRegistry,
                         KothLoreBoardPreview boardPreview) {
        this.parser = parser;
        this.msg = msg;
        this.cache = cache;
        this.wandService = wandService;
        this.kothRegistry = kothRegistry;
        this.boardPreview = boardPreview;
    }

    public void setGuiService(GuiService guiService) {
        this.guiService = guiService;
    }

    public void setTeamTogglesVisible(boolean visible) {
        this.teamTogglesVisible = visible;
    }

    @Override
    public void open(Player player) {
        cache.addKothToRegister(player.getUniqueId());
        currentTab.putIfAbsent(player.getUniqueId(), TAB_BASIC);
        Gui gui = buildGui(player);
        openGuis.put(player.getUniqueId(), gui);
        gui.open(player);
    }

    @Override
    protected void registerAllUpdaters() {
        // tabbed; per-tab updaters are registered dynamically each render
    }

    private Gui buildGui(Player player) {
        boolean editing = isEditingExisting(player);
        Gui gui = Gui.gui()
                .title(parser.parse(msg.getMessage(editing
                        ? "gui.create-koth.title-edit"
                        : "gui.create-koth.title-new")))
                .rows(6)
                .create();

        clearItemUpdaters();
        registerTabBar();
        registerFooter(editing);
        registerCurrentTabContent(player);

        setupAllItems(gui, player);
        fillContentBackground(gui);
        fillFooterBackground(gui);
        return gui;
    }

    private boolean isEditingExisting(Player player) {
        var data = cache.getKothToRegister(player.getUniqueId());
        return data != null
                && data.getId() != null
                && !data.getId().equals("example")
                && kothRegistry.get(data.getId()) != null;
    }

    // ===== Tab bar =====
    private void registerTabBar() {
        registerItemUpdater(SLOT_TAB_BASIC, (p, g) -> tabButton(p, "gui.create-koth.tab-basic", Material.WRITABLE_BOOK, TAB_BASIC));
        registerItemUpdater(SLOT_TAB_TIME, (p, g) -> tabButton(p, "gui.create-koth.tab-time", Material.CLOCK, TAB_TIME));
        registerItemUpdater(SLOT_TAB_REWARD, (p, g) -> tabButton(p, "gui.create-koth.tab-reward", Material.CHEST, TAB_REWARD));
        registerItemUpdater(SLOT_CLOSE, (p, g) -> ItemBuilder.from(Material.BARRIER)
                .name(parser.parse(msg.getMessage("gui.create-koth.close-name")))
                .lore(parser.parse(msg.getMessage("gui.create-koth.close-lore")))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    p.closeInventory();
                }));
    }

    private GuiItem tabButton(Player player, String labelKey, Material mat, int tab) {
        boolean active = currentTab.getOrDefault(player.getUniqueId(), TAB_BASIC) == tab;
        String marker = msg.getMessage(active
                ? "gui.create-koth.tab-active-marker"
                : "gui.create-koth.tab-inactive-marker");
        ItemBuilder b = ItemBuilder.from(active ? Material.LIME_STAINED_GLASS_PANE : mat)
                .name(parser.parse(marker + msg.getMessage(labelKey)))
                .lore(parser.parse(msg.getMessage(active
                        ? "gui.create-koth.tab-current-lore"
                        : "gui.create-koth.tab-switch-lore")));
        if (active) b.glow();
        return b.asGuiItem(event -> {
            event.setCancelled(true);
            currentTab.put(player.getUniqueId(), tab);
            refreshAll(player);
        });
    }

    // ===== Footer =====
    private void registerFooter(boolean editing) {
        registerItemUpdater(SLOT_F_TEST, this::footerTestButton);
        registerItemUpdater(SLOT_F_COPY, this::footerCopyButton);
        registerItemUpdater(SLOT_F_VALIDATION, this::footerValidation);
        registerItemUpdater(SLOT_F_SAVE, (p, g) -> footerSaveButton(p, g, editing));
    }

    private GuiItem footerTestButton(Player player, Gui gui) {
        var v = cache.validateKoth(player.getUniqueId());
        boolean ok = v.valid();
        return ItemBuilder.from(ok ? Material.FIREWORK_ROCKET : Material.GRAY_DYE)
                .name(parser.parse(msg.getMessage(ok ? "gui.create-koth.test-on-name" : "gui.create-koth.test-off-name")))
                .lore(parser.parse(msg.getMessage(ok ? "gui.create-koth.test-on-lore" : "gui.create-koth.test-off-lore")))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    if (!ok) return;
                    var tempId = cache.getKothToRegister(player.getUniqueId()).getId();
                    var koth = kothRegistry.get(tempId);
                    if (koth == null) {
                        player.sendMessage(parser.parse(msg.getMessage("gui.create-koth.chat-test-need-save")));
                        return;
                    }
                    try {
                        koth.start();
                        player.sendMessage(parser.parse(msg.fmt("gui.create-koth.chat-test-started", tempId)));
                        player.closeInventory();
                    } catch (Throwable t) {
                        player.sendMessage(parser.parse(msg.fmt("gui.create-koth.chat-test-failed", t.getMessage())));
                    }
                });
    }

    private GuiItem footerCopyButton(Player player, Gui gui) {
        boolean hasOthers = !kothRegistry.getAll().isEmpty();
        List<String> lore = hasOthers
                ? List.of(msg.getMessage("gui.create-koth.copy-on-lore-1"), msg.getMessage("gui.create-koth.copy-on-lore-2"))
                : List.of(msg.getMessage("gui.create-koth.copy-off-lore-1"), msg.getMessage("gui.create-koth.copy-off-lore-2"));
        return ItemBuilder.from(hasOthers ? Material.WRITTEN_BOOK : Material.GRAY_DYE)
                .name(parser.parse(msg.getMessage(hasOthers ? "gui.create-koth.copy-on-name" : "gui.create-koth.copy-off-name")))
                .lore(parser.parseList(lore))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    if (!hasOthers) return;
                    player.closeInventory();
                    if (guiService != null) {
                        guiService.openEditKothListGui(player);
                    }
                });
    }

    private GuiItem footerValidation(Player player, Gui gui) {
        KothValidation.ValidationResult result = cache.validateKoth(player.getUniqueId());
        List<String> lore = new ArrayList<>();
        if (result.valid()) {
            lore.add(msg.getMessage("gui.create-koth.validation-ok-line"));
        } else {
            lore.add(msg.fmt("gui.create-koth.validation-fail-line", result.errors().size()));
            for (String err : result.errors()) {
                lore.add(msg.fmt("gui.create-koth.validation-fail-item", translateError(err)));
            }
        }
        var data = cache.getKothToRegister(player.getUniqueId());
        if (data != null && data.getCaptureTime() > data.getMaxTime()) {
            lore.add(msg.fmt("gui.create-koth.validation-capture-exceeds",
                    data.getCaptureTime(), data.getMaxTime()));
        }
        return ItemBuilder.from(result.valid() ? Material.LIME_CONCRETE : Material.RED_CONCRETE)
                .name(parser.parse(msg.getMessage(result.valid()
                        ? "gui.create-koth.validation-ok-name"
                        : "gui.create-koth.validation-fail-name")))
                .lore(parser.parseList(lore))
                .asGuiItem(event -> event.setCancelled(true));
    }

    private GuiItem footerSaveButton(Player player, Gui gui, boolean editing) {
        KothValidation.ValidationResult v = cache.validateKoth(player.getUniqueId());
        var data = cache.getKothToRegister(player.getUniqueId());
        boolean captureValid = data != null && data.getCaptureTime() <= data.getMaxTime();
        boolean idClash = !editing && data != null && data.getId() != null
                && kothRegistry.get(data.getId()) != null;
        boolean canSave = v.valid() && captureValid && !idClash;

        Material mat = !canSave ? Material.GRAY_DYE
                : (editing ? Material.ANVIL : Material.SLIME_BALL);
        String name = msg.getMessage(editing
                ? "gui.create-koth.save-edit-name"
                : "gui.create-koth.save-create-name");

        List<String> lore = new ArrayList<>();
        if (canSave) {
            lore.add(msg.getMessage(editing ? "gui.create-koth.save-edit-lore" : "gui.create-koth.save-create-lore"));
        } else {
            lore.add(msg.getMessage("gui.create-koth.save-disabled-lore"));
        }
        if (idClash) lore.add(msg.getMessage("gui.create-koth.save-disabled-id-clash"));
        if (!captureValid) lore.add(msg.getMessage("gui.create-koth.save-disabled-capture-exceeds"));

        ItemBuilder b = ItemBuilder.from(mat).name(parser.parse(name)).lore(parser.parseList(lore));
        if (canSave) b.glow();

        return b.asGuiItem(event -> {
            event.setCancelled(true);
            if (!canSave) {
                player.sendMessage(parser.parse(msg.getMessage("gui.create-koth.chat-save-fail")));
                return;
            }
            player.closeInventory();
            if (editing) {
                kothRegistry.unregister(data.getId());
            }
            cache.buildKoth(player.getUniqueId());
            cache.removeKothToRegister(player.getUniqueId());
            currentTab.remove(player.getUniqueId());
            openGuis.remove(player.getUniqueId());
            player.sendMessage(parser.parse(msg.getMessage(editing
                    ? "gui.create-koth.chat-save-edited"
                    : "gui.create-koth.chat-save-created")));
        });
    }

    // ===== Tab content =====
    private void registerCurrentTabContent(Player player) {
        int tab = currentTab.getOrDefault(player.getUniqueId(), TAB_BASIC);
        switch (tab) {
            case TAB_BASIC -> registerBasicTab();
            case TAB_TIME -> registerTimeTab();
            case TAB_REWARD -> registerRewardTab();
        }
    }

    private void registerBasicTab() {
        registerItemUpdater(SLOT_B_ID, this::itemId);
        registerItemUpdater(SLOT_B_NAME, this::itemDisplayName);
        registerItemUpdater(SLOT_B_WAND, this::itemWand);
        registerItemUpdater(SLOT_B_TYPE, this::itemType);
        registerItemUpdater(SLOT_B_NOTIFY, this::itemNotify);
        if (teamTogglesVisible) {
            registerItemUpdater(SLOT_B_SOLO, this::itemSolo);
            registerItemUpdater(SLOT_B_DENY, this::itemDenyEnter);
            registerItemUpdater(SLOT_B_CREATE, this::itemCreateTeam);
        }
    }

    private void registerTimeTab() {
        registerItemUpdater(SLOT_T_MAX, this::itemMaxTime);
        registerItemUpdater(SLOT_T_CAP, this::itemCaptureTime);
        registerItemUpdater(SLOT_T_BOSS, this::itemBossbar);
        registerItemUpdater(SLOT_T_SCB_TOGGLE, this::itemScoreboardToggle);
        registerItemUpdater(SLOT_T_WAIT_TITLE, this::itemWaitingBoardTitle);
        registerItemUpdater(SLOT_T_WAIT_LINES, this::itemWaitingBoardLines);
        registerItemUpdater(SLOT_T_CAP_TITLE, this::itemCapturingBoardTitle);
        registerItemUpdater(SLOT_T_CAP_LINES, this::itemCapturingBoardLines);
    }

    private void registerRewardTab() {
        registerItemUpdater(SLOT_R_REWARDS, this::itemRewards);
        registerItemUpdater(SLOT_R_POOL_INFO, this::itemPoolInfo);
        registerItemUpdater(SLOT_R_COMMANDS, this::itemCommands);
    }

    // ===== Factories =====
    private GuiItem promptItem(Player player, Material mat, String nameKey, String currentText,
                               boolean required, Consumer<String> setter,
                               Function<String, String> validator) {
        boolean filled = currentText != null && !currentText.isBlank() && !"example".equals(currentText);
        List<String> lore = new ArrayList<>();
        lore.add(msg.fmt("gui.common.current-prefix", filled ? currentText : msg.getMessage("gui.common.not-set")));
        if (required) lore.add(msg.getMessage(filled ? "gui.common.required-yes" : "gui.common.required-no"));
        lore.add(msg.getMessage("gui.common.click-to-edit-anvil"));
        return ItemBuilder.from(mat)
                .name(parser.parse(msg.getMessage(nameKey)))
                .lore(parser.parseList(lore))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    openAnvilPrompt(player, currentText, setter, validator);
                });
    }

    private void openAnvilPrompt(Player player, String initial,
                                 Consumer<String> setter,
                                 Function<String, String> validator) {
        AnvilTextInput anvil = AnvilTextInput.get();
        if (anvil == null) {
            player.sendMessage(parser.parse(msg.getMessage("gui.create-koth.chat-anvil-uninit")));
            return;
        }
        anvil.prompt(player, initial == null ? "" : initial,
                text -> {
                    String err = validator == null ? null : validator.apply(text);
                    if (err != null) {
                        player.sendMessage(parser.parse("<red>" + err));
                        openAnvilPrompt(player, text, setter, validator);
                        return;
                    }
                    setter.accept(text);
                    open(player);
                },
                () -> open(player));
    }

    private GuiItem numericItem(Player player, Material mat, String nameKey, int value,
                                Consumer<Integer> setter) {
        return ItemBuilder.from(mat)
                .name(parser.parse(msg.fmt(nameKey, value)))
                .lore(parser.parseList(List.of(
                        msg.getMessage("gui.create-koth.numeric-hint-1"),
                        msg.getMessage("gui.create-koth.numeric-hint-2"),
                        msg.getMessage("gui.create-koth.numeric-hint-3"))))
                .glow()
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    int delta = numericDelta(event);
                    if (delta != 0) {
                        setter.accept(Math.max(1, value + delta));
                        refreshActive(player);
                    }
                });
    }

    private GuiItem toggleItem(Player player, String nameKey, boolean current, Runnable toggle) {
        String label = msg.getMessage(current ? "gui.create-koth.toggle-on" : "gui.create-koth.toggle-off");
        String coloured = msg.fmt(current ? "gui.create-koth.toggle-on-coloured" : "gui.create-koth.toggle-off-coloured", label);
        return ItemBuilder.from(current ? Material.LIME_DYE : Material.GRAY_DYE)
                .name(parser.parse("<yellow>" + msg.getMessage(nameKey)))
                .lore(parser.parse(msg.fmt("gui.create-koth.toggle-current", coloured)))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    toggle.run();
                    refreshActive(player);
                });
    }

    // ===== Tab 1: Basic =====
    private GuiItem itemId(Player player, Gui gui) {
        var data = cache.getKothToRegister(player.getUniqueId());
        return promptItem(player, Material.BAMBOO_SIGN, "gui.create-koth.id-name",
                data.getId(), true, data::setId,
                text -> {
                    if (text == null || text.isBlank()) return msg.getMessage("gui.create-koth.id-error-empty");
                    if (!text.matches("^[A-Za-z0-9_-]+$")) return msg.getMessage("gui.create-koth.id-error-charset");
                    var current = cache.getKothToRegister(player.getUniqueId()).getId();
                    if (!text.equals(current) && kothRegistry.get(text) != null) return msg.getMessage("gui.create-koth.id-error-clash");
                    return null;
                });
    }

    private GuiItem itemDisplayName(Player player, Gui gui) {
        var data = cache.getKothToRegister(player.getUniqueId());
        return promptItem(player, Material.CHERRY_SIGN, "gui.create-koth.display-name-name",
                data.getDisplayName(), true, data::setDisplayName,
                text -> (text == null || text.isBlank()) ? msg.getMessage("gui.create-koth.display-name-error-empty") : null);
    }

    private GuiItem itemWand(Player player, Gui gui) {
        var area = cache.getKothToRegister(player.getUniqueId()).getArea();
        List<String> lore = new ArrayList<>();
        if (area == null) {
            lore.add(msg.getMessage("gui.create-koth.area-not-set"));
            lore.add(msg.getMessage("gui.create-koth.area-click-wand"));
        } else {
            lore.add(msg.getMessage("gui.create-koth.area-set-ok"));
            lore.add(msg.fmt("gui.create-koth.area-world", area.worldName()));
            lore.add(msg.fmt("gui.create-koth.area-corner1", area.corner1().toString()));
            lore.add(msg.fmt("gui.create-koth.area-corner2", area.corner2().toString()));
        }
        return ItemBuilder.from(Material.BONE)
                .name(parser.parse(msg.getMessage("gui.create-koth.area-name")))
                .lore(parser.parseList(lore))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    wandService.giveWand(player);
                    player.closeInventory();
                });
    }

    private GuiItem itemType(Player player, Gui gui) {
        var data = cache.getKothToRegister(player.getUniqueId());
        boolean capture = data.isCaptureType();
        String label = msg.getMessage(capture ? "gui.create-koth.type-capture" : "gui.create-koth.type-score");
        return ItemBuilder.from(capture ? Material.DIAMOND_SWORD : Material.GOLDEN_AXE)
                .flags(ItemFlag.HIDE_ATTRIBUTES)
                .name(parser.parse(msg.fmt("gui.create-koth.type-name", label)))
                .lore(parser.parse(msg.getMessage("gui.create-koth.type-toggle-lore")))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    data.setType(capture ? KothType.SCORE : KothType.CAPTURE);
                    refreshActive(player);
                });
    }

    private GuiItem itemNotify(Player player, Gui gui) {
        var data = cache.getKothToRegister(player.getUniqueId());
        NotifyType current = data.getNotifyType();
        return ItemBuilder.from(Material.BELL)
                .name(parser.parse(msg.fmt("gui.create-koth.notify-name", current.getDisplayName())))
                .lore(parser.parse(msg.getMessage("gui.create-koth.notify-toggle-lore")))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    NotifyType[] values = NotifyType.values();
                    data.setNotifyType(values[(current.ordinal() + 1) % values.length]);
                    refreshActive(player);
                });
    }

    private GuiItem itemSolo(Player player, Gui gui) {
        var data = cache.getKothToRegister(player.getUniqueId());
        boolean solo = data.isSolo();
        String label = msg.getMessage(solo ? "gui.create-koth.solo-mode-solo" : "gui.create-koth.solo-mode-team");
        return ItemBuilder.from(solo ? Material.PLAYER_HEAD : Material.PAINTING)
                .name(parser.parse(msg.fmt("gui.create-koth.solo-name", label)))
                .lore(parser.parse(msg.getMessage("gui.create-koth.solo-toggle-lore")))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    data.setSolo(!data.isSolo());
                    refreshActive(player);
                });
    }

    private GuiItem itemDenyEnter(Player player, Gui gui) {
        var data = cache.getKothToRegister(player.getUniqueId());
        return toggleItem(player, "gui.create-koth.deny-enter-toggle", data.isDenyEnterWithoutTeam(),
                () -> data.setDenyEnterWithoutTeam(!data.isDenyEnterWithoutTeam()));
    }

    private GuiItem itemCreateTeam(Player player, Gui gui) {
        var data = cache.getKothToRegister(player.getUniqueId());
        return toggleItem(player, "gui.create-koth.create-team-toggle", data.isCreateTeamIfNotExistsOnEnter(),
                () -> data.setCreateTeamIfNotExistsOnEnter(!data.isCreateTeamIfNotExistsOnEnter()));
    }

    // ===== Tab 2: Time & Scoreboard =====
    private GuiItem itemMaxTime(Player player, Gui gui) {
        var data = cache.getKothToRegister(player.getUniqueId());
        return numericItem(player, Material.CLOCK, "gui.create-koth.max-time-name", data.getMaxTime(),
                data::setMaxTime);
    }

    private GuiItem itemCaptureTime(Player player, Gui gui) {
        var data = cache.getKothToRegister(player.getUniqueId());
        return numericItem(player, Material.COMPASS, "gui.create-koth.capture-time-name", data.getCaptureTime(),
                data::setCaptureTime);
    }

    private GuiItem itemBossbar(Player player, Gui gui) {
        var data = cache.getKothToRegister(player.getUniqueId());
        return toggleItem(player, "gui.create-koth.bossbar-toggle", data.isBossbarEnabled(),
                () -> data.setBossbarEnabled(!data.isBossbarEnabled()));
    }

    private GuiItem itemScoreboardToggle(Player player, Gui gui) {
        var data = cache.getKothToRegister(player.getUniqueId());
        return toggleItem(player, "gui.create-koth.scoreboard-toggle", data.isScoreboardEnabled(),
                () -> data.setScoreboardEnabled(!data.isScoreboardEnabled()));
    }

    private GuiItem itemWaitingBoardTitle(Player player, Gui gui) {
        var data = cache.getKothToRegister(player.getUniqueId());
        return promptItem(player, Material.PAPER, "gui.create-koth.board-waiting-title-name",
                data.getScoreboardWaitingTitle(), false,
                t -> { data.setScoreboardWaitingTitle(t); boardPreview.invalidate(player.getUniqueId()); },
                null);
    }

    private GuiItem itemWaitingBoardLines(Player player, Gui gui) {
        return ItemBuilder.from(Material.WRITABLE_BOOK)
                .name(parser.parse(msg.getMessage("gui.create-koth.board-waiting-lines-name")))
                .lore(boardPreview.getWaitingLore(player.getUniqueId()))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    if (guiService != null) guiService.openScoreboardLineEditor(player, false);
                });
    }

    private GuiItem itemCapturingBoardTitle(Player player, Gui gui) {
        var data = cache.getKothToRegister(player.getUniqueId());
        return promptItem(player, Material.NAME_TAG, "gui.create-koth.board-capturing-title-name",
                data.getScoreboardCapturingTitle(), false,
                t -> { data.setScoreboardCapturingTitle(t); boardPreview.invalidate(player.getUniqueId()); },
                null);
    }

    private GuiItem itemCapturingBoardLines(Player player, Gui gui) {
        return ItemBuilder.from(Material.BOOK)
                .name(parser.parse(msg.getMessage("gui.create-koth.board-capturing-lines-name")))
                .lore(boardPreview.getCapturingLore(player.getUniqueId()))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    if (guiService != null) guiService.openScoreboardLineEditor(player, true);
                });
    }

    // ===== Tab 3: Rewards & Commands =====
    private GuiItem itemRewards(Player player, Gui gui) {
        var data = cache.getKothToRegister(player.getUniqueId());
        return ItemBuilder.from(Material.DIAMOND)
                .name(parser.parse(msg.fmt("gui.create-koth.rewards-name", data.getPhysicalRewards().size())))
                .lore(parser.parse(msg.getMessage("gui.create-koth.rewards-lore")))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    guiService.openAddPhysicalRewardGui(player);
                });
    }

    private GuiItem itemPoolInfo(Player player, Gui gui) {
        return ItemBuilder.from(Material.ENDER_PEARL)
                .name(parser.parse(msg.getMessage("gui.create-koth.pool-info-name")))
                .lore(parser.parseList(List.of(
                        msg.getMessage("gui.create-koth.pool-info-lore-1"),
                        msg.getMessage("gui.create-koth.pool-info-lore-2"),
                        msg.getMessage("gui.create-koth.pool-info-lore-3"))))
                .asGuiItem(event -> event.setCancelled(true));
    }

    private GuiItem itemCommands(Player player, Gui gui) {
        return ItemBuilder.from(Material.COMMAND_BLOCK)
                .name(parser.parse(msg.getMessage("gui.create-koth.commands-name")))
                .lore(parser.parse(msg.getMessage("gui.create-koth.commands-lore")))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    guiService.openCommandGui(player);
                });
    }

    // ===== Helpers =====
    private int numericDelta(InventoryClickEvent event) {
        ClickType c = event.getClick();
        if (c == ClickType.MIDDLE) return 60;
        if (c.isRightClick() && c.isShiftClick()) return 10;
        if (c.isLeftClick() && c.isShiftClick()) return -10;
        if (c.isRightClick()) return 1;
        if (c.isLeftClick()) return -1;
        return 0;
    }

    private void fillContentBackground(Gui gui) {
        GuiItem filler = ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE)
                .name(parser.parse(" "))
                .asGuiItem(e -> e.setCancelled(true));
        for (int slot = CONTENT_FIRST; slot <= CONTENT_LAST; slot++) {
            if (gui.getGuiItem(slot) == null) gui.setItem(slot, filler);
        }
    }

    private void fillFooterBackground(Gui gui) {
        GuiItem filler = ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE)
                .name(parser.parse(" "))
                .asGuiItem(e -> e.setCancelled(true));
        for (int slot = FOOTER_FIRST; slot <= FOOTER_LAST; slot++) {
            if (gui.getGuiItem(slot) == null) gui.setItem(slot, filler);
        }
    }

    private void refreshAll(Player player) {
        Gui gui = openGuis.get(player.getUniqueId());
        if (gui == null) return;
        clearItemUpdaters();
        registerTabBar();
        registerFooter(isEditingExisting(player));
        registerCurrentTabContent(player);
        // Clear triumph-gui's logical item map, not just the backing Bukkit
        // inventory. update() repopulates from getGuiItems(); if we only cleared
        // the inventory, items from the previous tab (whose slots the new tab
        // does not overwrite) would be re-rendered and overlap. See PR #4 review.
        gui.clearItems();
        setupAllItems(gui, player);
        fillContentBackground(gui);
        fillFooterBackground(gui);
        gui.update();
    }

    private void refreshActive(Player player) {
        refreshAll(player);
    }

    private String translateError(String raw) {
        return switch (raw) {
            case "ID cannot be empty" -> msg.getMessage("gui.create-koth.err-name-id");
            case "ID may only contain letters, digits, '_' and '-'" -> msg.getMessage("gui.create-koth.err-name-id");
            case "Display name cannot be empty" -> msg.getMessage("gui.create-koth.err-name-display-name");
            case "Max time must be greater than 0" -> msg.getMessage("gui.create-koth.err-name-max-time");
            case "Capture time must be greater than 0" -> msg.getMessage("gui.create-koth.err-name-capture-time");
            case "Capture time must not exceed max time" -> msg.getMessage("gui.create-koth.err-name-capture-time");
            case "Area must be set" -> msg.getMessage("gui.create-koth.err-name-area");
            case "KOTH type must be set" -> msg.getMessage("gui.create-koth.err-name-type");
            default -> raw;
        };
    }
}
