package dev.smartshub.shkoth.api.reward;

public interface RewardPersistence {
    void saveReward(String kothId, int rewardId, String itemData, int amount);
    int getNextRewardId(String kothId);
}