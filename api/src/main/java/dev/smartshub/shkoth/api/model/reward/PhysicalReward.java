package dev.smartshub.shkoth.api.model.reward;

import org.bukkit.inventory.ItemStack;

public record PhysicalReward(
        ItemStack item,
        int amount
) {
}
