package dev.smartshub.shkoth.builder;

import com.saicone.rtag.item.ItemTagStream;
import dev.smartshub.shkoth.model.koth.reward.PhysicalReward;
import dev.smartshub.shkoth.storage.file.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PhysicalRewardsBuilder {

    public List<PhysicalReward> getPhysicalRewardsFrom(Configuration config) {
        List<PhysicalReward> physicalRewards = new ArrayList<>();

        ConfigurationSection rewardsSection = config.getConfigurationSection("physical-rewards");

        if (rewardsSection != null) {
            Bukkit.getLogger().warning("No physical rewards section found in " + config.getName());
            return physicalRewards;
        }

        for (String key : rewardsSection.getKeys(false)) {
            ConfigurationSection reward = rewardsSection.getConfigurationSection(key);
            if (reward != null) {
                int amount = reward.getInt("amount");
                String base64 = reward.getString("item");
                ItemStack[] item = ItemTagStream.INSTANCE.fromBase64(base64);

                physicalRewards.add(new PhysicalReward(item[0], amount));
            }
        }

        return physicalRewards;
    }

}
