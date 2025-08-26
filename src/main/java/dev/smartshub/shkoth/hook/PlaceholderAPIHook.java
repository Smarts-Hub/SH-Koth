package dev.smartshub.shkoth.hook;

import dev.smartshub.shkoth.registry.KothRegistry;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final KothRegistry kothRegistry;

    public PlaceholderAPIHook(KothRegistry kothRegistry) {
        this.kothRegistry = kothRegistry;
    }

    // Temp storage for contextual arguments, like player name or koth name in some messages
    private static volatile String tempArg1;
    private static volatile String tempArg2;
    private static volatile String tempArg3;

    public static void pushArgs(String... args) {
        tempArg1 = args.length > 0 ? args[0] : null;
        tempArg2 = args.length > 1 ? args[1] : null;
        tempArg3 = args.length > 2 ? args[2] : null;
    }

    public static void clearArgs() {
        tempArg1 = tempArg2 = tempArg3 = null;
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
        return switch (identifier) {
            case "arg1" -> tempArg1 != null ? tempArg1 : "";
            case "arg2" -> tempArg2 != null ? tempArg2 : "";
            case "arg3" -> tempArg3 != null ? tempArg3 : "";
            default -> "";
        };
    }
}
