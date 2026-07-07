package dev.smartshub.shkoth.gui;

import dev.smartshub.shkoth.api.reward.PhysicalReward;
import dev.smartshub.shkoth.message.MessageParser;
import dev.smartshub.shkoth.message.MessageRepository;
import dev.smartshub.shkoth.service.gui.GuiService;
import dev.smartshub.shkoth.service.gui.menu.cache.KothToRegisterCache;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.StorageGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AddPhysicalRewardGui {

    private final KothToRegisterCache kothToRegisterCache;
    private final MessageParser parser;
    private final MessageRepository msg;
    private GuiService guiService;

    private static final int SAVE_BUTTON_SLOT = 35;
    private static final int DISCARD_BUTTON_SLOT = 27;
    private static final int FILLER_START_SLOT = 28;
    private static final int FILLER_END_SLOT = 34;

    public AddPhysicalRewardGui(KothToRegisterCache kothToRegisterCache,
                                MessageParser parser, MessageRepository msg) {
        this.kothToRegisterCache = kothToRegisterCache;
        this.parser = parser;
        this.msg = msg;
    }

    public void open(Player player) {
        StorageGui gui = Gui.storage()
                .title(parser.parse(msg.getMessage("gui.add-reward.title")))
                .rows(4)
                .create();

        loadExistingRewards(gui, player);
        setupControlButtons(gui, player);
        fillControlArea(gui);

        gui.open(player);
    }

    private void loadExistingRewards(StorageGui gui, Player player) {
        var existingRewards = kothToRegisterCache.getKothToRegister(player.getUniqueId()).getPhysicalRewards();

        for (PhysicalReward reward : existingRewards) {
            ItemStack itemToAdd = reward.item().clone();
            itemToAdd.setAmount(reward.amount());

            for (int i = 0; i < gui.getInventory().getSize(); i++) {
                if (!isControlSlot(i) && gui.getInventory().getItem(i) == null) {
                    gui.getInventory().setItem(i, itemToAdd);
                    break;
                }
            }
        }
    }

    private void setupControlButtons(StorageGui gui, Player player) {
        gui.setItem(SAVE_BUTTON_SLOT, createSaveButton(gui, player));
        gui.setItem(DISCARD_BUTTON_SLOT, createDiscardButton(gui, player));
    }

    private GuiItem createSaveButton(StorageGui gui, Player player) {
        return ItemBuilder.from(Material.EMERALD_BLOCK)
                .name(parser.parse(msg.getMessage("gui.add-reward.save-name")))
                .lore(
                        parser.parse(msg.getMessage("gui.add-reward.save-lore-1")),
                        parser.parse(msg.getMessage("gui.add-reward.save-lore-2")),
                        Component.empty(),
                        parser.parse(msg.getMessage("gui.add-reward.save-lore-3")),
                        parser.parse(msg.getMessage("gui.add-reward.save-lore-4"))
                )
                .glow()
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    saveRewards(gui, player);
                });
    }

    private GuiItem createDiscardButton(StorageGui gui, Player player) {
        return ItemBuilder.from(Material.REDSTONE_BLOCK)
                .name(parser.parse(msg.getMessage("gui.add-reward.discard-name")))
                .lore(
                        parser.parse(msg.getMessage("gui.add-reward.discard-lore-1")),
                        parser.parse(msg.getMessage("gui.add-reward.discard-lore-2")),
                        Component.empty(),
                        parser.parse(msg.getMessage("gui.add-reward.discard-lore-3"))
                )
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    discardChanges(gui, player);
                });
    }

    private void fillControlArea(StorageGui gui) {
        GuiItem filler = createFillerItem();
        for (int i = FILLER_START_SLOT; i <= FILLER_END_SLOT; i++) {
            if (i != SAVE_BUTTON_SLOT && i != DISCARD_BUTTON_SLOT) {
                gui.setItem(i, filler);
            }
        }
    }

    private GuiItem createFillerItem() {
        return ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE)
                .name(Component.empty())
                .asGuiItem(event -> event.setCancelled(true));
    }

    private void saveRewards(StorageGui gui, Player player) {
        List<PhysicalReward> rewards = new ArrayList<>();
        for (int i = 0; i < gui.getInventory().getSize(); i++) {
            if (isControlSlot(i)) continue;
            ItemStack item = gui.getInventory().getItem(i);
            if (isValidRewardItem(item)) {
                ItemStack cleanItem = item.clone();
                rewards.add(new PhysicalReward(cleanItem, cleanItem.getAmount()));
            }
        }

        var kothData = kothToRegisterCache.getKothToRegister(player.getUniqueId());
        kothData.clearPhysicalRewards();
        kothData.setPhysicalRewards(rewards);

        player.sendMessage(parser.parse(msg.fmt("gui.add-reward.saved-chat", rewards.size())));
        guiService.openCreateKothGui(player);
    }

    private void discardChanges(StorageGui gui, Player player) {
        for (int i = 0; i < gui.getInventory().getSize(); i++) {
            if (!isControlSlot(i)) {
                gui.getInventory().setItem(i, null);
            }
        }
        player.sendMessage(parser.parse(msg.getMessage("gui.add-reward.discarded-chat")));
        guiService.openCreateKothGui(player);
    }

    private boolean isControlSlot(int slot) {
        return slot == SAVE_BUTTON_SLOT || slot == DISCARD_BUTTON_SLOT ||
                (slot >= FILLER_START_SLOT && slot <= FILLER_END_SLOT);
    }

    private boolean isValidRewardItem(ItemStack item) {
        return item != null && item.getType() != Material.AIR &&
                item.getType() != Material.GRAY_STAINED_GLASS_PANE;
    }

    public void setGuiService(GuiService guiService) {
        this.guiService = guiService;
    }
}
