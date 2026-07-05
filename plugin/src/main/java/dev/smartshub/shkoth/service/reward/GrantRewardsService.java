package dev.smartshub.shkoth.service.reward;

import dev.smartshub.shkoth.api.koth.Koth;
import dev.smartshub.shkoth.api.reward.PhysicalReward;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GrantRewardsService {

    private final Koth koth;

    public GrantRewardsService(Koth koth) {
        this.koth = koth;
    }

    public void grantRewards() {
        Set<UUID> winners = koth.getWinners();
        List<PhysicalReward> rewards = koth.getPhysicalRewards();
        if (rewards.isEmpty() || winners.isEmpty()) {
            return;
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        OfflineRewardStorage offlineStorage = OfflineRewardStorage.get();

        for (UUID winnerId : winners) {
            ItemStack item = createItemStack(rewards.get(random.nextInt(rewards.size())));
            Player online = Bukkit.getPlayer(winnerId);
            if (online != null && online.isOnline()) {
                grantToPlayer(online, item);
            } else if (offlineStorage != null) {
                offlineStorage.add(winnerId, item);
            } else {
                logger().warning("Offline reward storage is not initialized; reward lost (winner=" + winnerId + ")");
            }
        }
    }

    private ItemStack createItemStack(PhysicalReward reward) {
        ItemStack item = reward.item().clone();
        item.setAmount(reward.amount());
        return item;
    }

    private void grantToPlayer(Player player, ItemStack item) {
        try {
            Map<Integer, ItemStack> leftover = player.getInventory().addItem(item.clone());
            if (!leftover.isEmpty()) {
                dropProtected(player, leftover);
            }
        } catch (Exception e) {
            logger().log(Level.SEVERE, "Failed to grant reward to " + player.getName(), e);
        }
    }

    private void dropProtected(Player player, Map<Integer, ItemStack> leftoverItems) {
        try {
            for (ItemStack leftoverItem : leftoverItems.values()) {
                Item dropped = player.getWorld().dropItemNaturally(player.getLocation(), leftoverItem);
                try {
                    dropped.setOwner(player.getUniqueId());
                } catch (Throwable ignored) {
                    // Older APIs lack setOwner; the item still drops at the player's feet.
                }
            }
        } catch (Exception e) {
            logger().log(Level.SEVERE, "Failed to drop reward items for " + player.getName(), e);
        }
    }

    private Logger logger() {
        return JavaPlugin.getProvidingPlugin(GrantRewardsService.class).getLogger();
    }
}
