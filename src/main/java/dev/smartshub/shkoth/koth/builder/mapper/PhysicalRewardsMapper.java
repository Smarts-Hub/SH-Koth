package dev.smartshub.shkoth.koth.builder.mapper;

import com.saicone.rtag.item.ItemTagStream;
import dev.smartshub.shkoth.api.model.builder.mapper.Mapper;
import dev.smartshub.shkoth.api.model.config.ConfigContainer;
import dev.smartshub.shkoth.api.model.reward.PhysicalReward;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PhysicalRewardsMapper implements Mapper<List<PhysicalReward>, ConfigContainer> {

    @Override
    public List<PhysicalReward> map(ConfigContainer config) {
        List<PhysicalReward> physicalRewards = new ArrayList<>();

        ConfigurationSection rewardsSection = config.getConfigurationSection("physical-rewards");

        if (rewardsSection != null) {
            Bukkit.getLogger().warning("No physical rewards section found in " + config.getName());
            return physicalRewards;
        }

        for (String key : rewardsSection.getKeys(false)) {
            ConfigurationSection reward = rewardsSection.getConfigurationSection(key);
            if (reward == null) continue;

            int amount = reward.getInt("amount");
            String base64 = reward.getString("item");
            ItemStack[] item = ItemTagStream.INSTANCE.fromBase64(base64);

            physicalRewards.add(new PhysicalReward(item[0], amount));

        }

        return physicalRewards;
    }

}
