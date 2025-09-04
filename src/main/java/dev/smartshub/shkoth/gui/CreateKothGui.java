package dev.smartshub.shkoth.gui;

import dev.smartshub.shkoth.api.gui.BaseUpdatableGui;
import dev.smartshub.shkoth.api.koth.guideline.KothType;
import dev.smartshub.shkoth.service.gui.GuiService;
import dev.smartshub.shkoth.service.gui.menu.aux.KothLoreBoardPreview;
import dev.smartshub.shkoth.service.gui.menu.aux.WaitingToFill;
import dev.smartshub.shkoth.message.MessageParser;
import dev.smartshub.shkoth.service.gui.menu.cache.KothToRegisterCache;
import dev.smartshub.shkoth.service.wand.WandService;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.List;

public class CreateKothGui extends BaseUpdatableGui {

    private final MessageParser parser;
    private final KothToRegisterCache kothToRegisterCache;
    private final KothLoreBoardPreview kothLoreBoardPreview;
    private final WandService wandService;
    private GuiService guiService;

    public CreateKothGui(MessageParser parser, KothToRegisterCache kothToRegisterCache, WandService wandService) {
        this.parser = parser;
        this.kothToRegisterCache = kothToRegisterCache;
        this.wandService = wandService;
        this.kothLoreBoardPreview = new KothLoreBoardPreview(kothToRegisterCache, parser);
    }

    @Override
    public void open(Player player) {
        kothToRegisterCache.addKothToRegister(player.getUniqueId());

        Gui gui = Gui.gui()
                .title(parser.parse("<gold>Create KOTH"))
                .rows(5)
                .create();

        registerAllUpdaters();
        updateScoreboardItems(player, gui);
        setupAllItems(gui, player);
        fillEmpty(gui);

        gui.open(player);
    }

    public void updateScoreboardItems(Player player, Gui gui) {
        updateItem(gui, player, 23);
        updateItem(gui, player, 25);
    }

    @Override
    protected void registerAllUpdaters() {
        registerItemUpdater(10, this::createCreateKothItem);
        registerItemUpdater(11, this::createIdItem);
        registerItemUpdater(12, this::createDisplayNameItem);
        registerItemUpdater(13, this::createMaxTimeItem);
        registerItemUpdater(14, this::createCaptureTimeItem);
        registerItemUpdater(15, this::createWandItem);
        registerItemUpdater(16, this::createTypeItem);
        registerItemUpdater(19, this::createRewardsItem);
        registerItemUpdater(20, this::createSchedulesItem);
        registerItemUpdater(21, this::createCommandsItem);
        registerItemUpdater(22, this::createCapturingBoardTitleItem);
        registerItemUpdater(23, this::createCapturingBoardLinesItem);
        registerItemUpdater(24, this::createWaitingBoardTitleItem);
        registerItemUpdater(25, this::createWaitingBoardLinesItem);
        registerItemUpdater(29, this::createSoloItem);
        registerItemUpdater(30, this::createBossbarItem);
        registerItemUpdater(31, this::createDenyEnterItem);
        registerItemUpdater(32, this::createTeamItem);
        registerItemUpdater(33, this::createScoreboardItem);
    }

    private GuiItem createCreateKothItem(Player player, Gui gui) {
        return ItemBuilder.from(Material.SLIME_BALL)
                .name(parser.parse("<green>Create the KOTH"))
                .lore(parser.parse("<gray>Click to create the KOTH!"))
                .glow()
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    if(!kothToRegisterCache.validateKoth(player.getUniqueId()).valid()){
                        player.sendMessage(parser.parse("<red>You cannot create the KOTH yet! Make sure to set all required fields."));
                        return;
                    }
                    player.closeInventory();
                    kothToRegisterCache.buildKoth(player.getUniqueId());
                    kothToRegisterCache.removeKothToRegister(player.getUniqueId());
                    player.sendMessage(parser.parse("<green>You have successfully created the KOTH!"));
                });
    }

    private GuiItem createIdItem(Player player, Gui gui) {
        return ItemBuilder.from(Material.BAMBOO_SIGN)
                .name(parser.parse("<yellow>KOTH ID"))
                .lore(parser.parse("<gray>Click to set the KoTH Id!"),
                        parser.parse("<dark_gray>Current: <gray>" +
                                (kothToRegisterCache.getKothToRegister(player.getUniqueId()).getId() == null ?
                                        "Not set yet" : kothToRegisterCache.getKothToRegister(player.getUniqueId()).getId())))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    player.closeInventory();
                    kothToRegisterCache.setWaitingToFill(player.getUniqueId(), WaitingToFill.ID);
                    player.sendMessage(parser.parse("<green>Type the KOTH ID in chat, or <red>'cancel'<green> to return:"));
                });
    }

    private GuiItem createDisplayNameItem(Player player, Gui gui) {
        return ItemBuilder.from(Material.CHERRY_SIGN)
                .name(parser.parse("<yellow>KOTH Display Name"))
                .lore(parser.parse("<gray>Click to set the KoTH display name (MiniMessage)!"),
                        parser.parse("<dark_gray>Current: <gray>" +
                                (kothToRegisterCache.getKothToRegister(player.getUniqueId()).getId() == null ?
                                        "Not set yet" : kothToRegisterCache.getKothToRegister(player.getUniqueId()).getDisplayName())))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    player.closeInventory();
                    kothToRegisterCache.setWaitingToFill(player.getUniqueId(), WaitingToFill.DISPLAYNAME);
                    player.sendMessage(parser.parse("<green>Type the KOTH Display-Name in chat, or <red>'cancel'<green> to return:"));
                });
    }

    private GuiItem createMaxTimeItem(Player player, Gui gui) {
        return ItemBuilder.from(Material.CLOCK)
                .name(parser.parse("<yellow>KOTH Max Time"))
                .lore(parser.parse("<gray>Koth running time: " + kothToRegisterCache.getKothToRegister(player.getUniqueId()).getMaxTime() + " seconds"),
                        parser.parse("<dark_gray>Right Click: <gray>Increase by 1"),
                        parser.parse("<dark_gray>Left Click: <gray>Decrease by 1"),
                        parser.parse("<dark_gray>Shift Right Click: <gray>Increase by 10"),
                        parser.parse("<dark_gray>Shift Left Click: <gray>Decrease by 10"))
                .glow()
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    int change = getNumericChange(event);
                    if(change != 0) {
                        var kothData = kothToRegisterCache.getKothToRegister(player.getUniqueId());
                        kothData.setMaxTime(Math.max(1, kothData.getMaxTime() + change));
                        updateItem(gui, player, 13);
                    }
                });
    }

    private GuiItem createCaptureTimeItem(Player player, Gui gui) {
        return ItemBuilder.from(Material.COMPASS)
                .name(parser.parse("<yellow>KOTH Capture Time"))
                .lore(parser.parse("<gray>Koth capture time: " + kothToRegisterCache.getKothToRegister(player.getUniqueId()).getCaptureTime() + " seconds"),
                        parser.parse("<dark_gray>Right Click: <gray>Increase by 1"),
                        parser.parse("<dark_gray>Left Click: <gray>Decrease by 1"),
                        parser.parse("<dark_gray>Shift Right Click: <gray>Increase by 10"),
                        parser.parse("<dark_gray>Shift Left Click: <gray>Decrease by 10"))
                .glow()
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    int change = getNumericChange(event);
                    if(change != 0) {
                        var kothData = kothToRegisterCache.getKothToRegister(player.getUniqueId());
                        kothData.setCaptureTime(Math.max(1, kothData.getCaptureTime() + change));
                        updateItem(gui, player, 14);
                    }
                });
    }

    private GuiItem createWandItem(Player player, Gui gui) {
        var area = kothToRegisterCache.getKothToRegister(player.getUniqueId()).getArea();
        List<String> lore = new ArrayList<>();
        if(area == null){
            lore.add("<gray>Area not set yet! (Use the wand to set it)");
        } else {
            lore.add("<green>Set correctly!");
            lore.add("<light_purple>World: <yellow>" + area.worldName());
            lore.add("<gray>Corner1: <yellow>" + area.corner1().toString());
            lore.add("<gray>Corner2: <yellow>" + area.corner2().toString());
        }
        return ItemBuilder.from(Material.BONE)
                .name(parser.parse("<yellow>KOTH Wand"))
                .lore(parser.parseList(lore))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    wandService.giveWand(player);
                    player.closeInventory();
                });
    }

    private GuiItem createTypeItem(Player player, Gui gui) {
        Material mat = kothToRegisterCache.getKothToRegister(player.getUniqueId()).getType() == KothType.CAPTURE ?
                Material.DIAMOND_SWORD : Material.GOLDEN_AXE;
        return ItemBuilder.from(mat)
                .flags(ItemFlag.HIDE_ATTRIBUTES)
                .name(parser.parse("<yellow>Capture Type"))
                .lore(parser.parse("<gray>Current: " +
                        (kothToRegisterCache.getKothToRegister(player.getUniqueId()).isCaptureType() ? "Capture" : "Score")))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    var kothData = kothToRegisterCache.getKothToRegister(player.getUniqueId());
                    kothData.setType(kothData.isCaptureType() ? KothType.SCORE : KothType.CAPTURE);
                    updateItem(gui, player, 16);
                });
    }

    private GuiItem createRewardsItem(Player player, Gui gui) {
        return ItemBuilder.from(Material.DIAMOND)
                .name(parser.parse("<yellow>Koth rewards"))
                .lore(parser.parse("<gray>Open the gui to manage rewards"))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    guiService.openAddPhysicalRewardGui(player);
                });
    }

    private GuiItem createSchedulesItem(Player player, Gui gui) {
        return ItemBuilder.from(Material.LECTERN)
                .name(parser.parse("<yellow>Koth schedules"))
                .lore(parser.parse("<gray>Open the gui to manage schedules"))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    guiService.openCreateSchedulerGui(player);
                });
    }

    private GuiItem createCommandsItem(Player player, Gui gui) {
        return ItemBuilder.from(Material.COMMAND_BLOCK)
                .name(parser.parse("<yellow>Koth commands"))
                .lore(parser.parse("<gray>Open the gui to manage commands"))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    guiService.openCommandGui(player);
                });
    }

    private GuiItem createCapturingBoardTitleItem(Player player, Gui gui) {
        return ItemBuilder.from(Material.NAME_TAG)
                .name(parser.parse("<yellow>Capturing Scoreboard Title"))
                .lore(parser.parse("<gray>Click to set it!"))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    player.closeInventory();
                    kothToRegisterCache.setWaitingToFill(player.getUniqueId(), WaitingToFill.BOARD_CAPTURING_TITLE);
                    player.sendMessage(parser.parse("<green>Type the title in chat, or <red>'cancel'<green> to return:"));
                });
    }

    private GuiItem createCapturingBoardLinesItem(Player player, Gui gui) {
        return ItemBuilder.from(Material.BOOK)
                .name(parser.parse("<yellow>Capturing Scoreboard lines"))
                .lore(kothLoreBoardPreview.getCapturingLore(player.getUniqueId()))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    handleBoardLinesClick(event, player, gui, true, 23);
                });
    }

    private GuiItem createWaitingBoardTitleItem(Player player, Gui gui) {
        return ItemBuilder.from(Material.PAPER)
                .name(parser.parse("<yellow>Waiting Scoreboard Title"))
                .lore(parser.parse("<gray>Click to set it!"))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    player.closeInventory();
                    kothToRegisterCache.setWaitingToFill(player.getUniqueId(), WaitingToFill.BOARD_WAITING_TITLE);
                    player.sendMessage(parser.parse("<green>Type the title in chat, or <red>'cancel'<green> to return:"));
                });
    }

    private GuiItem createWaitingBoardLinesItem(Player player, Gui gui) {
        return ItemBuilder.from(Material.WRITABLE_BOOK)
                .name(parser.parse("<yellow>Waiting Scoreboard lines"))
                .lore(kothLoreBoardPreview.getWaitingLore(player.getUniqueId()))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    handleBoardLinesClick(event, player, gui, false, 25);
                });
    }

    private GuiItem createToggleItem(Player player, Gui gui, int slot, String name,
                                     boolean currentValue, String enabledText, String disabledText, Runnable toggleAction) {
        Material mat = currentValue ? Material.LIME_DYE : Material.GRAY_DYE;
        return ItemBuilder.from(mat)
                .name(parser.parse("<yellow>" + name))
                .lore(parser.parse("<gray>Current: " + (currentValue ? enabledText : disabledText)))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    toggleAction.run();
                    updateItem(gui, player, slot);
                });
    }

    private GuiItem createSoloItem(Player player, Gui gui) {
        return createToggleItem(player, gui, 29, "Enable/Disable solo mode",
                kothToRegisterCache.getKothToRegister(player.getUniqueId()).isSolo(),
                "Solo", "Team", () -> {
                    var kothData = kothToRegisterCache.getKothToRegister(player.getUniqueId());
                    kothData.setSolo(!kothData.isSolo());
                });
    }

    private GuiItem createBossbarItem(Player player, Gui gui) {
        return createToggleItem(player, gui, 30, "Enable/Disable bossbar",
                kothToRegisterCache.getKothToRegister(player.getUniqueId()).isBossbarEnabled(),
                "Enabled", "Disabled", () -> {
                    var kothData = kothToRegisterCache.getKothToRegister(player.getUniqueId());
                    kothData.setBossbarEnabled(!kothData.isBossbarEnabled());
                });
    }

    private GuiItem createDenyEnterItem(Player player, Gui gui) {
        return createToggleItem(player, gui, 31, "Enable/Disable deny enter if not in team",
                kothToRegisterCache.getKothToRegister(player.getUniqueId()).isDenyEnterWithoutTeam(),
                "Enabled", "Disabled", () -> {
                    var kothData = kothToRegisterCache.getKothToRegister(player.getUniqueId());
                    kothData.setDenyEnterWithoutTeam(!kothData.isDenyEnterWithoutTeam());
                });
    }

    private GuiItem createTeamItem(Player player, Gui gui) {
        return createToggleItem(player, gui, 32, "Enable/Disable create team if not exists on enter",
                kothToRegisterCache.getKothToRegister(player.getUniqueId()).isCreateTeamIfNotExistsOnEnter(),
                "Enabled", "Disabled", () -> {
                    var kothData = kothToRegisterCache.getKothToRegister(player.getUniqueId());
                    kothData.setCreateTeamIfNotExistsOnEnter(!kothData.isCreateTeamIfNotExistsOnEnter());
                });
    }

    private GuiItem createScoreboardItem(Player player, Gui gui) {
        return createToggleItem(player, gui, 33, "Enable/Disable Scoreboard",
                kothToRegisterCache.getKothToRegister(player.getUniqueId()).isScoreboardEnabled(),
                "Enabled", "Disabled", () -> {
                    var kothData = kothToRegisterCache.getKothToRegister(player.getUniqueId());
                    kothData.setScoreboardEnabled(!kothData.isScoreboardEnabled());
                });
    }


    private int getNumericChange(org.bukkit.event.inventory.InventoryClickEvent event) {
        if(event.getClick().isRightClick() && event.getClick().isShiftClick()) return 10;
        if(event.getClick().isLeftClick() && event.getClick().isShiftClick()) return -10;
        if(event.getClick().isRightClick()) return 1;
        if(event.getClick().isLeftClick()) return -1;
        return 0;
    }

    private void handleBoardLinesClick(org.bukkit.event.inventory.InventoryClickEvent event, Player player, Gui gui, boolean isCapturing, int slot) {
        if(event.getClick().isRightClick()){
            player.closeInventory();
            kothToRegisterCache.setWaitingToFill(player.getUniqueId(),
                    isCapturing ? WaitingToFill.BOARD_CAPTURING_LINE : WaitingToFill.BOARD_WAITING_LINE);
            player.sendMessage(parser.parse("<green>Type the line in chat, or <red>'cancel'<green> to return:"));
        } else if(event.getClick().isLeftClick()){
            var kothData = kothToRegisterCache.getKothToRegister(player.getUniqueId());
            if(isCapturing) kothData.removeLastCapturingLine();
            else kothData.removeLastWaitingLine();
            updateItem(gui, player, slot);
        } else if(event.getClick().isShiftClick()){
            var kothData = kothToRegisterCache.getKothToRegister(player.getUniqueId());
            if(isCapturing) kothData.clearCapturingLines();
            else kothData.clearWaitingLines();
            updateItem(gui, player, slot);
        }
    }

    public void setGuiService(GuiService guiService) {
        this.guiService = guiService;
    }
}