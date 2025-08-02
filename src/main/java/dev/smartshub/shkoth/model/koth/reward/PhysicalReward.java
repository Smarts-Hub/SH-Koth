package dev.smartshub.shkoth.model.koth.reward;

import org.bukkit.inventory.ItemStack;

public record PhysicalReward(
        ItemStack item,
        int amount
) {
}
