package dev.smartshub.shkoth.service.reward;

import com.saicone.rtag.item.ItemTagStream;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Buffers rewards for offline winners and grants them when the player next joins.
 * Writes happen asynchronously; the store is loaded synchronously once on enable so
 * the very first join event can already read from it.
 */
public class OfflineRewardStorage {

    private static volatile OfflineRewardStorage instance;

    private final Plugin plugin;
    private final File file;
    private final Map<UUID, List<ItemStack>> pending = new ConcurrentHashMap<>();

    public OfflineRewardStorage(Plugin plugin) {
        this.plugin = plugin;
        File folder = plugin.getDataFolder();
        if (!folder.exists()) folder.mkdirs();
        this.file = new File(folder, "offline-rewards.yml");
        loadSync();
        instance = this;
    }

    public static OfflineRewardStorage get() {
        return instance;
    }

    public void add(UUID winner, ItemStack item) {
        if (item == null) return;
        pending.computeIfAbsent(winner, u -> Collections.synchronizedList(new ArrayList<>())).add(item.clone());
        scheduleSave();
    }

    /**
     * Called when a player joins. Returns the ItemStacks owed to the player and
     * removes them from the buffer.
     */
    public List<ItemStack> drain(Player player) {
        List<ItemStack> items = pending.remove(player.getUniqueId());
        if (items == null || items.isEmpty()) return Collections.emptyList();
        scheduleSave();
        return new ArrayList<>(items);
    }

    public boolean hasPending(UUID id) {
        List<ItemStack> list = pending.get(id);
        return list != null && !list.isEmpty();
    }

    private void loadSync() {
        if (!file.exists()) return;
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection root = yaml.getConfigurationSection("players");
        if (root == null) return;

        for (String key : root.getKeys(false)) {
            try {
                UUID id = UUID.fromString(key);
                List<String> base64List = root.getStringList(key);
                List<ItemStack> items = new ArrayList<>(base64List.size());
                for (String base64 : base64List) {
                    ItemStack[] arr = ItemTagStream.INSTANCE.fromBase64(base64);
                    if (arr != null) {
                        for (ItemStack it : arr) {
                            if (it != null) items.add(it);
                        }
                    }
                }
                if (!items.isEmpty()) {
                    pending.put(id, Collections.synchronizedList(items));
                }
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Failed to read offline rewards (key=" + key + ")", e);
            }
        }
    }

    private void scheduleSave() {
        new BukkitRunnable() {
            @Override
            public void run() {
                saveNow();
            }
        }.runTaskAsynchronously(plugin);
    }

    private synchronized void saveNow() {
        YamlConfiguration yaml = new YamlConfiguration();
        Map<String, List<String>> serialized = new HashMap<>();
        for (Map.Entry<UUID, List<ItemStack>> e : pending.entrySet()) {
            List<ItemStack> items;
            synchronized (e.getValue()) {
                items = new ArrayList<>(e.getValue());
            }
            List<String> base64 = new ArrayList<>(items.size());
            for (ItemStack item : items) {
                base64.add(ItemTagStream.INSTANCE.toBase64(new ItemStack[]{item}));
            }
            serialized.put(e.getKey().toString(), base64);
        }
        yaml.set("players", serialized);
        try {
            yaml.save(file);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save offline rewards", ex);
        }
    }

    public void flush() {
        // Synchronous save on the main thread (used during onDisable).
        saveNow();
    }

    /**
     * Called when a player joins: grants the buffered rewards, dropping any that do
     * not fit at the player's feet, marked so only that player can pick them up.
     */
    public void giveTo(Player player) {
        List<ItemStack> items = drain(player);
        if (items.isEmpty()) return;
        for (ItemStack item : items) {
            Map<Integer, ItemStack> leftover = player.getInventory().addItem(item);
            for (ItemStack drop : leftover.values()) {
                org.bukkit.entity.Item dropped = player.getWorld().dropItemNaturally(player.getLocation(), drop);
                try {
                    dropped.setOwner(player.getUniqueId());
                } catch (Throwable ignored) {
                    // Older APIs lack setOwner; ignore.
                }
            }
        }
        // Localised message — keep a safe fallback that avoids depending on MessageRepository here
        // (this class is constructed before services are wired). The fallback is English.
        player.sendMessage("§aYou received " + items.size() + " pending KOTH reward(s)!");
    }
}
