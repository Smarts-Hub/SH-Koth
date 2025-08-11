package dev.smartshub.shkoth.koth.reward;

import dev.smartshub.shkoth.api.config.ConfigContainer;
import dev.smartshub.shkoth.api.config.ConfigType;
import dev.smartshub.shkoth.api.reward.RewardPersistence;
import dev.smartshub.shkoth.service.config.ConfigService;

import java.util.Set;

public class ConfigRewardPersistence implements RewardPersistence {
    private final ConfigService configService;
    
    public ConfigRewardPersistence(ConfigService configService) {
        this.configService = configService;
    }
    
    @Override
    public void saveReward(String kothId, int rewardId, String itemData, int amount) {
        ConfigContainer config = configService.provide("koths/" + kothId + ".yml", ConfigType.KOTH_DEFINITION);
        var section = config.getConfigurationSection("physical-rewards");
        
        String rewardPath = "physical-rewards." + rewardId;
        section.set(rewardPath + ".item", itemData);
        section.set(rewardPath + ".amount", amount);
        
        configService.save(ConfigType.KOTH_DEFINITION);
    }
    
    @Override
    public int getNextRewardId(String kothId) {
        ConfigContainer config = configService.provide("koths/" + kothId + ".yml", ConfigType.KOTH_DEFINITION);
        var section = config.getConfigurationSection("physical-rewards");
        Set<String> keys = section != null ? section.getKeys(false) : Set.of();
        
        int max = 0;
        for (String key : keys) {
            try {
                max = Math.max(max, Integer.parseInt(key));
            } catch (NumberFormatException ignored) {
            }
        }
        return max + 1;
    }
}