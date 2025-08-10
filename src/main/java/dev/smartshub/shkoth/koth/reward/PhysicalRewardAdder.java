package dev.smartshub.shkoth.koth.reward;

import com.saicone.rtag.item.ItemTagStream;
import dev.smartshub.shkoth.api.config.ConfigContainer;
import dev.smartshub.shkoth.api.config.ConfigType;
import dev.smartshub.shkoth.api.koth.Koth;
import dev.smartshub.shkoth.api.reward.PhysicalReward;
import dev.smartshub.shkoth.service.config.ConfigService;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;


public class PhysicalRewardAdder {

    public void addRewards(ConfigService configService, Koth koth, ItemStack item, int amount) {
        if (item == null || amount <= 0) {
            return;
        }

        koth.getPhysicalRewards().add(new PhysicalReward(item, amount));

        item.setAmount(amount);

        ConfigContainer config = configService.provide("koths/" + koth.getId() + ".yml", ConfigType.KOTH_DEFINITION);
        var section = config.getConfigurationSection("physical-rewards");
        int nextId = getNextRewardId(section != null ? section.getKeys(false) : Set.of());
        String base64Item = ItemTagStream.INSTANCE.listToBase64(List.of(item));
        section.set("physical-rewards." + nextId + ".item", base64Item);
        section.set("physical-rewards." + nextId + ".amount", amount);

        configService.save(ConfigType.KOTH_DEFINITION);
    }

    private int getNextRewardId(Set<String> keys) {
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
