package dev.smartshub.shkoth.koth.reward;

import dev.smartshub.shkoth.api.config.ConfigContainer;
import dev.smartshub.shkoth.api.config.ConfigType;
import dev.smartshub.shkoth.api.reward.RewardPersistence;
import dev.smartshub.shkoth.service.config.ConfigService;
import dev.smartshub.shkoth.storage.config.ConfigContainerImpl;
import dev.smartshub.shkoth.storage.config.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public class ConfigRewardPersistence implements RewardPersistence {
    private final ConfigService configService;

    public ConfigRewardPersistence(ConfigService configService) {
        this.configService = configService;
    }

    @Override
    public void saveReward(String kothId, int rewardId, String itemData, int amount) {
        ConfigContainer configContainer = configService.provide("koths/" + kothId + ".yml", ConfigType.KOTH_DEFINITION);

        org.bukkit.configuration.file.FileConfiguration config;
        if (configContainer instanceof ConfigContainerImpl impl) {
            config = impl.getInternalConfiguration();
        } else {
            throw new IllegalStateException("ConfigContainer not Supported");
        }

        if (config.getConfigurationSection("physical-rewards") == null) {
            config.createSection("physical-rewards");
        }

        String rewardPath = "physical-rewards." + rewardId;
        config.set(rewardPath + ".item", itemData);
        config.set(rewardPath + ".amount", amount);


        try {
            config.save(impl.getFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public int getNextRewardId(String kothId) {
        ConfigContainer configContainer = configService.provide("koths/" + kothId + ".yml", ConfigType.KOTH_DEFINITION);

        if (!(configContainer instanceof dev.smartshub.shkoth.storage.config.ConfigContainerImpl impl)) {
            throw new IllegalStateException("ConfigContainer not supported");
        }

        Configuration config = impl.getInternalConfiguration();

        if (config.getConfigurationSection("physical-rewards") == null) {
            config.createSection("physical-rewards");
            return 0;
        }

        Set<String> keys = config.getConfigurationSection("physical-rewards").getKeys(false);
        int max = -1;
        for (String key : keys) {
            try {
                max = Math.max(max, Integer.parseInt(key));
            } catch (NumberFormatException ignored) {
            }
        }
        return max + 1;
    }
}