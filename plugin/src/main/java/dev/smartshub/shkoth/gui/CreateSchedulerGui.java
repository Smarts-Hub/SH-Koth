package dev.smartshub.shkoth.gui;

import dev.smartshub.shkoth.api.gui.BaseUpdatableGui;
import dev.smartshub.shkoth.api.gui.item.ItemUpdater;
import dev.smartshub.shkoth.message.MessageParser;
import dev.smartshub.shkoth.message.MessageRepository;
import dev.smartshub.shkoth.service.gui.GuiService;
import dev.smartshub.shkoth.service.gui.menu.cache.KothToRegisterCache;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.time.DayOfWeek;

public class CreateSchedulerGui extends BaseUpdatableGui {

    private final KothToRegisterCache kothToRegisterCache;
    private final MessageParser parser;
    private final MessageRepository msg;
    private GuiService guiService;

    public CreateSchedulerGui(KothToRegisterCache kothToRegisterCache,
                              MessageParser messageParser, MessageRepository msg) {
        this.kothToRegisterCache = kothToRegisterCache;
        this.parser = messageParser;
        this.msg = msg;
    }

    public void updateItem(Gui gui, Player player, int slot) {
        ItemUpdater updater = itemUpdaters.get(slot);
        if (updater != null) {
            gui.updateItem(slot, updater.createItem(player, gui));
        }
    }

    public void registerItemUpdater(int slot, ItemUpdater updater) {
        itemUpdaters.put(slot, updater);
    }

    public void open(Player player) {
        DayOfWeek day = kothToRegisterCache.getKothToRegister(player.getUniqueId()).getTempDay();
        if (day == null) {
            day = DayOfWeek.MONDAY;
            kothToRegisterCache.getKothToRegister(player.getUniqueId()).setTempDay(day);
        }

        Gui gui = Gui.gui()
                .title(parser.parse(msg.getMessage("gui.scheduler.title")))
                .rows(2)
                .create();

        registerAllUpdaters();
        setupAllItems(gui, player);
        fillEmpty(gui);

        gui.open(player);
    }

    @Override
    protected void registerAllUpdaters() {
        registerItemUpdater(3, this::createDayItem);
        registerItemUpdater(4, this::createHourItem);
        registerItemUpdater(5, this::createMinuteItem);
        registerItemUpdater(9, this::createBackItem);
    }

    private GuiItem createDayItem(Player player, Gui gui) {
        DayOfWeek day = kothToRegisterCache.getKothToRegister(player.getUniqueId()).getTempDay();
        if (day == null) day = DayOfWeek.MONDAY;

        DayOfWeek finalDay = day;
        String dayName = msg.getMessage("gui.scheduler.day-" + day.name().toLowerCase());

        return ItemBuilder.from(Material.PAPER)
                .name(parser.parse(msg.fmt("gui.scheduler.day-name", dayName)))
                .lore(parser.parse(msg.getMessage("gui.scheduler.day-lore")))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    DayOfWeek next = finalDay.plus(1);
                    kothToRegisterCache.getKothToRegister(player.getUniqueId()).setTempDay(next);
                    updateItem(gui, player, 3);
                });
    }

    private GuiItem createHourItem(Player player, Gui gui) {
        int hour = kothToRegisterCache.getKothToRegister(player.getUniqueId()).getHour();

        return ItemBuilder.from(Material.CLOCK)
                .name(parser.parse(msg.fmt("gui.scheduler.hour-name", hour)))
                .lore(
                        parser.parse(msg.getMessage("gui.scheduler.hour-hint-add")),
                        parser.parse(msg.getMessage("gui.scheduler.hour-hint-sub"))
                )
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    int currentHour = kothToRegisterCache.getKothToRegister(player.getUniqueId()).getHour();
                    int newHour;
                    if (event.isRightClick()) {
                        newHour = (currentHour + 1) % 24;
                    } else if (event.isLeftClick()) {
                        newHour = (currentHour - 1 + 24) % 24;
                    } else {
                        return;
                    }
                    kothToRegisterCache.getKothToRegister(player.getUniqueId()).setHour(newHour);
                    updateItem(gui, player, 4);
                });
    }

    private GuiItem createMinuteItem(Player player, Gui gui) {
        int minute = kothToRegisterCache.getKothToRegister(player.getUniqueId()).getMinute();

        return ItemBuilder.from(Material.REDSTONE)
                .name(parser.parse(msg.fmt("gui.scheduler.minute-name", minute)))
                .lore(
                        parser.parse(msg.getMessage("gui.scheduler.minute-hint-add")),
                        parser.parse(msg.getMessage("gui.scheduler.minute-hint-sub")),
                        parser.parse(msg.getMessage("gui.scheduler.minute-hint-add-10")),
                        parser.parse(msg.getMessage("gui.scheduler.minute-hint-sub-10"))
                )
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    int currentMinute = kothToRegisterCache.getKothToRegister(player.getUniqueId()).getMinute();
                    int change = 0;
                    if (event.isRightClick() && event.isShiftClick()) change = 10;
                    else if (event.isLeftClick() && event.isShiftClick()) change = -10;
                    else if (event.isRightClick()) change = 1;
                    else if (event.isLeftClick()) change = -1;

                    if (change != 0) {
                        int newMinute = (currentMinute + change + 60) % 60;
                        kothToRegisterCache.getKothToRegister(player.getUniqueId()).setMinute(newMinute);
                        updateItem(gui, player, 5);
                    }
                });
    }

    private GuiItem createBackItem(Player player, Gui gui) {
        return ItemBuilder.from(Material.ARROW)
                .name(parser.parse(msg.getMessage("gui.scheduler.back-name")))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    guiService.openCreateKothGui(player);
                });
    }

    public void setGuiService(GuiService guiService) {
        this.guiService = guiService;
    }
}
