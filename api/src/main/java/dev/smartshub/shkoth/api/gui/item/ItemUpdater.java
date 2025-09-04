package dev.smartshub.shkoth.api.gui.item;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;

@FunctionalInterface
    public interface ItemUpdater {
        GuiItem createItem(Player player, Gui gui);
    }