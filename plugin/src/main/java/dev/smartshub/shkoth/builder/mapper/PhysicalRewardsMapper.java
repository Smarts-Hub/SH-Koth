package dev.smartshub.shkoth.builder.mapper;

import com.saicone.rtag.item.ItemTagStream;
import dev.smartshub.shkoth.api.builder.mapper.Mapper;
import dev.smartshub.shkoth.api.config.ConfigContainer;
import dev.smartshub.shkoth.api.reward.PhysicalReward;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class PhysicalRewardsMapper implements Mapper<List<PhysicalReward>, ConfigContainer> {

    @Override
    public List<PhysicalReward> map(ConfigContainer config) {
        List<PhysicalReward> physicalRewards = new ArrayList<>();

        ConfigurationSection rewardsSection = config.getConfigurationSection("physical-rewards");

        if (rewardsSection == null) {
            JavaPlugin.getProvidingPlugin(PhysicalRewardsMapper.class).getLogger().warning("No physical rewards section found in " + config.getName());
            return physicalRewards;
        }

        for (String key : rewardsSection.getKeys(false)) {
            ConfigurationSection reward = rewardsSection.getConfigurationSection(key);
            if (reward == null) continue;

            int amount = reward.getInt("amount");
            String base64 = reward.getString("item");
            if (base64 == null) continue;

            Object[] rawItems = ItemTagStream.INSTANCE.fromBase64(base64);
            if (rawItems.length == 0) continue;

            if (rawItems[0] instanceof ItemStack itemStack) {
                physicalRewards.add(new PhysicalReward(itemStack, amount));
            } else {
                JavaPlugin.getProvidingPlugin(PhysicalRewardsMapper.class).getLogger().warning("Reward at key " + key + " is not an ItemStack in " + config.getName());
            }
        }

        return physicalRewards;
    }

}
