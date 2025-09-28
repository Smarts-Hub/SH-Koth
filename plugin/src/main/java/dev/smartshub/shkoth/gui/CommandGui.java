package dev.smartshub.shkoth.gui;

import dev.smartshub.shkoth.api.gui.BaseUpdatableGui;
import dev.smartshub.shkoth.api.koth.command.Commands;
import dev.smartshub.shkoth.service.gui.GuiService;
import dev.smartshub.shkoth.service.gui.menu.other.WaitingToFill;
import dev.smartshub.shkoth.service.gui.menu.cache.KothToRegisterCache;
import dev.smartshub.shkoth.message.MessageParser;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandGui extends BaseUpdatableGui {

    private final KothToRegisterCache kothToRegisterCache;
    private final MessageParser parser;
    private GuiService guiService;

    public CommandGui(KothToRegisterCache kothToRegisterCache, MessageParser parser) {
        this.kothToRegisterCache = kothToRegisterCache;
        this.parser = parser;
    }

    @Override
    protected void registerAllUpdaters() {
        registerItemUpdater(3, this::createWinnerCommandsItem);
        registerItemUpdater(4, this::createStartCommandsItem);
        registerItemUpdater(5, this::createEndCommandsItem);
        registerItemUpdater(9, this::createBackItem);
        registerItemUpdater(17, this::createSaveItem);
    }

    @Override
    public void open(Player player) {
        Gui gui = Gui.gui()
                .title(parser.parse("<gold>Configure Commands"))
                .rows(2)
                .create();

        registerAllUpdaters();
        setupAllItems(gui, player);
        fillEmpty(gui);

        gui.open(player);
    }

    private GuiItem createWinnerCommandsItem(Player player, Gui gui) {
        List<Component> winCommands = createCommandLore("winner",
                kothToRegisterCache.getKothToRegister(player.getUniqueId()).getWinnersCommands());

        return ItemBuilder.from(Material.DIAMOND)
                .name(parser.parse("<green>Winner Commands"))
                .lore(winCommands)
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    handleCommandAction(player, gui, event, WaitingToFill.WIN_COMMAND,
                            () -> kothToRegisterCache.getKothToRegister(player.getUniqueId()).clearWinnerCommands(), 3);
                });
    }

    private GuiItem createStartCommandsItem(Player player, Gui gui) {
        List<Component> startCommands = createCommandLore("start",
                kothToRegisterCache.getKothToRegister(player.getUniqueId()).getStartCommands());

        return ItemBuilder.from(Material.EMERALD)
                .name(parser.parse("<yellow>Start Commands"))
                .lore(startCommands)
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    handleCommandAction(player, gui, event, WaitingToFill.START_COMMAND,
                            () -> kothToRegisterCache.getKothToRegister(player.getUniqueId()).clearStartCommands(), 4);
                });
    }

    private GuiItem createEndCommandsItem(Player player, Gui gui) {
        List<Component> endCommands = createCommandLore("end",
                kothToRegisterCache.getKothToRegister(player.getUniqueId()).getEndCommands());

        return ItemBuilder.from(Material.REDSTONE)
                .name(parser.parse("<red>End Commands"))
                .lore(endCommands)
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    handleCommandAction(player, gui, event, WaitingToFill.END_COMMAND,
                            () -> kothToRegisterCache.getKothToRegister(player.getUniqueId()).clearEndCommands(), 5);
                });
    }

    private GuiItem createSaveItem(Player player, Gui gui) {
        return ItemBuilder.from(Material.EMERALD_BLOCK)
                .name(parser.parse("<green>Save commands"))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    kothToRegisterCache.getKothToRegister(player.getUniqueId()).buildCommands();
                    guiService.openCreateKothGui(player);
                });
    }

    private GuiItem createBackItem(Player player, Gui gui) {
        return ItemBuilder.from(Material.ARROW)
                .name(parser.parse("<red>Back to Creating Menu"))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    UUID uuid = player.getUniqueId();
                    var kothData = kothToRegisterCache.getKothToRegister(uuid);

                    kothData.setCommands(
                            new Commands(
                                    kothData.getStartCommands(),
                                    kothData.getEndCommands(),
                                    kothData.getWinnersCommands()
                            )
                    );
                    guiService.openCreateKothGui(player);
                });
    }

    private List<Component> createCommandLore(String type, List<String> commands) {
        List<Component> lore = new ArrayList<>();
        lore.add(parser.parse("<gray>Right Click: <white>Add command"));
        lore.add(parser.parse("<gray>Left Click: <white>Clear all"));
        lore.add(parser.parse("<gray>Current " + type + " commands:"));

        commands.forEach(command -> {
            lore.add(parser.parse("<gray>- " + command));
        });

        if (commands.isEmpty()) {
            lore.add(parser.parse("<dark_gray>No commands set"));
        }

        return lore;
    }

    private void handleCommandAction(Player player, Gui gui,
                                     org.bukkit.event.inventory.InventoryClickEvent event,
                                     WaitingToFill waitingType, Runnable clearAction, int slot) {
        if (event.isRightClick()) {
            player.closeInventory();
            kothToRegisterCache.getKothToRegister(player.getUniqueId()).setWaitingToFill(waitingType);
            player.sendMessage(parser.parse("<green>Type the command in chat, or <red>'cancel'<green> to return:"));
        } else if (event.isLeftClick()) {
            clearAction.run();
            updateItem(gui, player, slot);
        }
    }

    public void setGuiService(GuiService guiService) {
        this.guiService = guiService;
    }
}