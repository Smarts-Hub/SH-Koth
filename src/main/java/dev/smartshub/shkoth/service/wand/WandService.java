package dev.smartshub.shkoth.service.wand;

import dev.smartshub.shkoth.SHKoth;
import dev.smartshub.shkoth.message.MessageParser;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class WandService {

    private final MessageParser parser;
    private final ItemStack wandItem;
    private final NamespacedKey wandKey;

    public WandService(SHKoth plugin, MessageParser parser) {
        this.parser = parser;
        this.wandKey = new NamespacedKey(plugin, "koth_wand");
        this.wandItem = createWand();
    }

    private ItemStack createWand() {
        ItemStack item = new ItemStack(Material.BONE);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.displayName(parser.parse("<gold>Selection wand"));
            meta.lore(List.of(
                    parser.parse("<dark_gray>Right click: <gray>set position 1"),
                    parser.parse("<dark_gray>Left click: <gray>set position 2"),
                    parser.parse("<dark_gray>Shift click: <gray>save and return to menu")));
            meta.getPersistentDataContainer().set(wandKey, PersistentDataType.BYTE, (byte) 1);
            item.setItemMeta(meta);
        }

        return item;
    }

    public ItemStack getWandItem() {
        return wandItem.clone();
    }

    public void giveWand(Player player) {
        player.getInventory().addItem(getWandItem());
        player.sendMessage(parser.parse("<green>You have received the KOTH Wand!"));
    }

    public boolean isWand(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        if (!item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(wandKey, PersistentDataType.BYTE);
    }
}
