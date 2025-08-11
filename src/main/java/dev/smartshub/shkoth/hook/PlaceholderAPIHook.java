package dev.smartshub.shkoth.hook;

import dev.smartshub.shkoth.registry.KothRegistry;
import dev.smartshub.shkoth.registry.cache.SimpleKothCache;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final KothRegistry kothRegistry;
    private final SimpleKothCache kothCache;

    public PlaceholderAPIHook(KothRegistry kothRegistry, SimpleKothCache kothCache) {
        this.kothRegistry = kothRegistry;
        this.kothCache = kothCache;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "shkoth";
    }

    @Override
    public @NotNull String getAuthor() {
        return "SmartsHub";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {

        return "";
    }
}
