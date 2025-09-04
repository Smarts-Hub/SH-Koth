package dev.smartshub.shkoth.gui;

import dev.smartshub.shkoth.api.gui.BaseUpdatableGui;
import dev.smartshub.shkoth.api.gui.item.ItemUpdater;
import dev.smartshub.shkoth.api.location.schedule.Schedule;

import dev.smartshub.shkoth.service.gui.GuiService;
import dev.smartshub.shkoth.service.gui.menu.cache.KothToRegisterCache;
import dev.smartshub.shkoth.message.MessageParser;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CreateSchedulerGui extends BaseUpdatableGui {

    private final KothToRegisterCache kothToRegisterCache;
    private final MessageParser parser;
    private GuiService guiService;

    public CreateSchedulerGui(KothToRegisterCache kothToRegisterCache, MessageParser messageParser) {
        this.kothToRegisterCache = kothToRegisterCache;
        this.parser = messageParser;
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
                .title(parser.parse("<gold>Create KoTH Schedule"))
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
        registerItemUpdater(16, this::createClearItem);
        registerItemUpdater(17, this::createSaveItem);
    }

    private GuiItem createDayItem(Player player, Gui gui) {
        DayOfWeek day = kothToRegisterCache.getKothToRegister(player.getUniqueId()).getTempDay();
        if (day == null) day = DayOfWeek.MONDAY;

        DayOfWeek finalDay = day;
        return ItemBuilder.from(Material.PAPER)
                .name(parser.parse("<green>Day: <gray>" + day.name().toLowerCase()))
                .lore(parser.parse("<dark_gray>Click: <gray>Next Day"))
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
                .name(parser.parse("<yellow>Hour: " + hour))
                .lore(parser.parse("<dark_gray>Right Click: <gray>Increase by 1"),
                        parser.parse("<dark_gray>Left Click: <gray>Decrease by 1"))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    int currentHour = kothToRegisterCache.getKothToRegister(player.getUniqueId()).getHour();
                    int newHour;

                    if(event.isRightClick()){
                        newHour = (currentHour + 1) % 24;
                    } else if(event.isLeftClick()){
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
                .name(parser.parse("<aqua>Minutes: " + minute))
                .lore(parser.parse("<dark_gray>Right Click: <gray>Increase by 1"),
                        parser.parse("<dark_gray>Left Click: <gray>Decrease by 1"),
                        parser.parse("<dark_gray>Shift Right Click: <gray>Increase by 10"),
                        parser.parse("<dark_gray>Shift Left Click: <gray>Decrease by 10"))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    int currentMinute = kothToRegisterCache.getKothToRegister(player.getUniqueId()).getMinute();
                    int change = 0;

                    if(event.isRightClick() && event.isShiftClick()) {
                        change = 10;
                    } else if(event.isLeftClick() && event.isShiftClick()) {
                        change = -10;
                    } else if(event.isRightClick()) {
                        change = 1;
                    } else if(event.isLeftClick()) {
                        change = -1;
                    }

                    if(change != 0) {
                        int newMinute = (currentMinute + change + 60) % 60;
                        kothToRegisterCache.getKothToRegister(player.getUniqueId()).setMinute(newMinute);
                        updateItem(gui, player, 5);
                    }
                });
    }

    private GuiItem createBackItem(Player player, Gui gui) {
        return ItemBuilder.from(Material.ARROW)
                .name(parser.parse("<red>Back to Creating Menu"))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    guiService.openCreateKothGui(player);
                });
    }

    private GuiItem createClearItem(Player player, Gui gui) {
        List<Component> schedules = getCurrentSchedulesLore(player);

        return ItemBuilder.from(Material.BARRIER)
                .name(parser.parse("<dark_red>Clear All Schedulers"))
                .lore(schedules)
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    kothToRegisterCache.getKothToRegister(player.getUniqueId()).getSchedules().clear();
                    updateItem(gui, player, 16);
                    updateItem(gui, player, 17);
                });
    }

    private GuiItem createSaveItem(Player player, Gui gui) {
        return ItemBuilder.from(Material.EMERALD_BLOCK)
                .name(parser.parse("<green>Save Scheduler"))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    var kothData = kothToRegisterCache.getKothToRegister(player.getUniqueId());

                    Schedule newSchedule = new Schedule(
                            kothData.getTempDay(),
                            LocalTime.of(kothData.getHour(), kothData.getMinute())
                    );

                    kothData.addSchedule(newSchedule);

                    updateItem(gui, player, 16);

                    player.sendMessage(parser.parse("<green>Schedule saved successfully!"));
                });
    }

    private List<Component> getCurrentSchedulesLore(Player player) {
        List<Component> schedules = new ArrayList<>();
        schedules.add(parser.parse("<gray>Current Schedules:"));

        var currentSchedules = kothToRegisterCache.getKothToRegister(player.getUniqueId()).getSchedules();

        if (currentSchedules.isEmpty()) {
            schedules.add(parser.parse("<gray>- <dark_gray>No schedules created yet"));
        } else {
            currentSchedules.forEach(schedule -> {
                var dayName = schedule.day().name();
                var hourName = schedule.time().toString();
                schedules.add(parser.parse("<gray>- <yellow>" + dayName + " <dark_gray>| <aqua>" + hourName));
            });
        }

        return schedules;
    }

    public void setGuiService(GuiService guiService) {
        this.guiService = guiService;
    }
}