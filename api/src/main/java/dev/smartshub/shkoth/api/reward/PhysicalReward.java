package dev.smartshub.shkoth.api.reward;

import org.bukkit.inventory.ItemStack;

public record PhysicalReward(
        ItemStack item,
        int amount
) {
}
