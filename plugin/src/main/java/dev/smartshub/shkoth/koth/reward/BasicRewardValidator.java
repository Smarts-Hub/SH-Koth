package dev.smartshub.shkoth.koth.reward;

import dev.smartshub.shkoth.api.reward.RewardValidator;
import org.bukkit.inventory.ItemStack;

public class BasicRewardValidator implements RewardValidator {
    @Override
    public boolean isValid(ItemStack item, int amount) {
        return item != null && amount > 0;
    }
}