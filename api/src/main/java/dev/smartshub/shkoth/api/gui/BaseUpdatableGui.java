package dev.smartshub.shkoth.api.gui;

import dev.smartshub.shkoth.api.gui.item.ItemUpdater;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseUpdatableGui {
    
    protected final Map<Integer, ItemUpdater> itemUpdaters = new HashMap<>();

    protected void updateItem(Gui gui, Player player, int slot) {
        ItemUpdater updater = itemUpdaters.get(slot);
        if (updater != null) {
            gui.updateItem(slot, updater.createItem(player, gui));
        }
    }

    protected void registerItemUpdater(int slot, ItemUpdater updater) {
        itemUpdaters.put(slot, updater);
    }

    protected void updateItems(Gui gui, Player player, int... slots) {
        for (int slot : slots) {
            updateItem(gui, player, slot);
        }
    }

    protected void clearItemUpdaters() {
        itemUpdaters.clear();
    }

    protected void setupAllItems(Gui gui, Player player) {
        itemUpdaters.forEach((slot, updater) -> {
            gui.setItem(slot, updater.createItem(player, gui));
        });
    }

    protected GuiItem createFillerItem() {
        return ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE)
                .name(Component.empty())
                .asGuiItem(event -> event.setCancelled(true));
    }

    protected void fillEmpty(Gui gui) {
        gui.getFiller().fill(createFillerItem());
    }

    public abstract void open(Player player);

    protected abstract void registerAllUpdaters();
}