package dev.smartshub.shkoth.koth.reward;

import dev.smartshub.shkoth.api.koth.Koth;
import dev.smartshub.shkoth.api.reward.PhysicalReward;
import dev.smartshub.shkoth.api.reward.RewardPersistence;
import dev.smartshub.shkoth.api.reward.RewardValidator;
import dev.smartshub.shkoth.builder.serializer.RTagItemSerializer;
import org.bukkit.inventory.ItemStack;

public class PhysicalRewardAdder {
    private final RewardPersistence rewardPersistence;
    private final RTagItemSerializer itemSerializer;
    private final RewardValidator rewardValidator;

    public PhysicalRewardAdder(RewardPersistence rewardPersistence,
                               RTagItemSerializer itemSerializer,
                               RewardValidator rewardValidator) {
        this.rewardPersistence = rewardPersistence;
        this.itemSerializer = itemSerializer;
        this.rewardValidator = rewardValidator;
    }

    public void addRewards(Koth koth, ItemStack item, int amount) {
        if (!rewardValidator.isValid(item, amount)) {
            return;
        }

        addRewardToKoth(koth, item, amount);

        persistReward(koth.getId(), item, amount);
    }

    private void addRewardToKoth(Koth koth, ItemStack item, int amount) {
        ItemStack rewardItem = item.clone();
        rewardItem.setAmount(amount);
        koth.getPhysicalRewards().add(new PhysicalReward(rewardItem, amount));
    }

    private void persistReward(String kothId, ItemStack item, int amount) {
        int rewardId = rewardPersistence.getNextRewardId(kothId);
        String serializedItem = itemSerializer.serialize(createItemWithAmount(item, amount));
        rewardPersistence.saveReward(kothId, rewardId, serializedItem, amount);
    }

    private ItemStack createItemWithAmount(ItemStack item, int amount) {
        ItemStack itemWithAmount = item.clone();
        itemWithAmount.setAmount(amount);
        return itemWithAmount;
    }
}
