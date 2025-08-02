package dev.smartshub.shkoth.model.reward;

import org.bukkit.inventory.ItemStack;

public record PhysicalReward(
        ItemStack item,
        int amount
) {
}
