package dev.smartshub.shkoth.gui;

import dev.smartshub.shkoth.api.koth.Koth;
import dev.smartshub.shkoth.message.MessageParser;
import dev.smartshub.shkoth.message.MessageRepository;
import dev.smartshub.shkoth.registry.KothRegistry;
import dev.smartshub.shkoth.service.gui.GuiService;
import dev.smartshub.shkoth.service.gui.menu.cache.KothToRegisterCache;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class EditKothListGui {

    private final KothRegistry kothRegistry;
    private final KothToRegisterCache kothToRegisterCache;
    private final MessageParser parser;
    private final MessageRepository msg;
    private GuiService guiService;

    public EditKothListGui(KothRegistry kothRegistry, KothToRegisterCache kothToRegisterCache,
                           MessageParser parser, MessageRepository msg) {
        this.kothRegistry = kothRegistry;
        this.kothToRegisterCache = kothToRegisterCache;
        this.parser = parser;
        this.msg = msg;
    }

    public void open(Player player) {
        PaginatedGui gui = Gui.paginated()
                .title(parser.parse(msg.getMessage("gui.edit-list.title")))
                .rows(6)
                .pageSize(45)
                .create();

        gui.setDefaultClickAction(event -> event.setCancelled(true));

        for (Koth koth : kothRegistry.getAll()) {
            List<Component> lore = new ArrayList<>();
            lore.add(parser.parse(msg.fmt("gui.edit-list.item-lore-display", koth.getDisplayName())));
            lore.add(parser.parse(msg.fmt("gui.edit-list.item-lore-type", koth.getType().name())));
            lore.add(parser.parse(msg.fmt("gui.edit-list.item-lore-state", koth.getState().name())));
            lore.add(Component.empty());
            lore.add(parser.parse(msg.getMessage("gui.edit-list.item-lore-action")));

            gui.addItem(ItemBuilder.from(Material.BEACON)
                    .name(parser.parse(msg.fmt("gui.edit-list.item-name", koth.getId())))
                    .lore(lore)
                    .asGuiItem(event -> {
                        player.closeInventory();
                        kothToRegisterCache.addKothToRegister(player.getUniqueId());
                        var tempData = kothToRegisterCache.getKothToRegister(player.getUniqueId());
                        if (tempData != null && koth instanceof dev.smartshub.shkoth.koth.Koth impl) {
                            tempData.loadFromKoth(impl);
                        }
                        if (guiService != null) {
                            guiService.openCreateKothGui(player);
                        }
                    }));
        }

        gui.setItem(6, 3, ItemBuilder.from(Material.ARROW)
                .name(parser.parse(msg.getMessage("gui.edit-list.prev-page")))
                .asGuiItem(event -> gui.previous()));

        gui.setItem(6, 7, ItemBuilder.from(Material.ARROW)
                .name(parser.parse(msg.getMessage("gui.edit-list.next-page")))
                .asGuiItem(event -> gui.next()));

        gui.open(player);
    }

    public void setGuiService(GuiService guiService) {
        this.guiService = guiService;
    }
}
