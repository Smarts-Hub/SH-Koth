package dev.smartshub.shkoth.service.reward;

import dev.smartshub.shkoth.api.model.koth.Koth;
import dev.smartshub.shkoth.api.model.reward.PhysicalReward;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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

        List<Player> onlineWinners = getOnlineWinners();

        if (onlineWinners.isEmpty()) {
            return;
        }

        List<ItemStack> processedItems = prepareRewardItems();

        for (Player player : onlineWinners) {
            grantRewardsToPlayer(player, processedItems);
        }
    }

    private List<Player> getOnlineWinners() {
        Set<UUID> winners = koth.getWinners();
        return winners.stream()
                .map(Bukkit::getPlayer)
                .filter(player -> player != null && player.isOnline())
                .toList();
    }

    private List<ItemStack> prepareRewardItems() {
        List<PhysicalReward> rewards = koth.getPhysicalRewards();
        return rewards.stream()
                .map(this::createItemStack)
                .toList();
    }

    private ItemStack createItemStack(PhysicalReward reward) {
        ItemStack item = reward.item().clone();
        item.setAmount(reward.amount());
        return item;
    }

    private void grantRewardsToPlayer(Player player, List<ItemStack> items) {
        try {
            for (ItemStack item : items) {
                ItemStack playerItem = item.clone();
                addItemToPlayerInventory(player, playerItem);
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe( "Error granting rewards to player " + player.getName());
        }
    }

    private void addItemToPlayerInventory(Player player, ItemStack item) {
        Map<Integer, ItemStack> leftover = player.getInventory().addItem(item);

        if (!leftover.isEmpty()) {
            dropLeftoverItems(player, leftover);
        }
    }

    private void dropLeftoverItems(Player player, Map<Integer, ItemStack> leftoverItems) {
        try {
            for (ItemStack leftoverItem : leftoverItems.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), leftoverItem);
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe("Failed to drop leftover items for player " + player.getName());
        }
    }
}
