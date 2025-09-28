package dev.smartshub.shkoth.builder.serializer;

import com.saicone.rtag.item.ItemTagStream;
import dev.smartshub.shkoth.api.builder.serializer.Serializer;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class RTagItemSerializer implements Serializer<String, ItemStack> {
    @Override
    public String serialize(ItemStack item) {
        return ItemTagStream.INSTANCE.listToBase64(List.of(item));
    }
}