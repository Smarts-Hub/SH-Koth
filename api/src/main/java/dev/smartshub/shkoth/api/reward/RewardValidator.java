package dev.smartshub.shkoth.api.reward;

import org.bukkit.inventory.ItemStack;

public interface RewardValidator {
    boolean isValid(ItemStack item, int amount);
}