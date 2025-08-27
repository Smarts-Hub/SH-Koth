package dev.smartshub.shkoth.hook.placeholder;

import dev.smartshub.shkoth.registry.KothRegistry;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final KothRegistry kothRegistry;
    private final PlaceholderHelper placeholderHelper = new PlaceholderHelper();

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

        String kothName = placeholderHelper.extractKothName(identifier);
        String placeholderType = placeholderHelper.extractPlaceholderType(identifier);

        // Non related to any koth, just general placeholders
        if(kothName == null){
            return switch (identifier) {
                case "arg1" -> tempArg1 != null ? tempArg1 : "";
                case "arg2" -> tempArg2 != null ? tempArg2 : "";
                case "arg3" -> tempArg3 != null ? tempArg3 : "";
                case "total_wins" -> "";
                case "next_koth" -> ""; // next scheduled koth display name
                case "next_koth_in" -> ""; // time until next koth starts
                case "next_koth_in_formatted" -> "";
                default -> "";
            };
        }

        // Related to a specific koth
        return switch (placeholderType) {
            case "is_active" -> kothRegistry.get(kothName).isRunning() ? "true" : "false";
            case "is_scheduled" -> kothRegistry.isKothScheduledNow(kothName) ? "true" : "false";
            case "is_solo" -> kothRegistry.get(kothName).isSolo() ? "true" : "false";
            case "next_schedule" -> "";
            case "next_schedule_formatted" -> "";
            case "display_name" -> "";
            case "id" -> "";
            case "world" -> "";
            case "x" -> "";
            case "y" -> "";
            case "z" -> "";
            case "is_running" -> "";
            case "is_capturing" -> "";
            case "capturer" -> ""; // Solo -> name, Team -> Leader's name  + koth formatting (pending to implement by config)
            case "captured" -> ""; // time in seconds or score
            case "captured_formatted" -> ""; // time formatted (no sense for score)
            case "max_time" -> "";
            case "max_time_formatted" -> "";
            case "time_left" -> "";
            case "time_left_formatted" -> "";
            case "time_taken" -> "";
            case "time_taken_formatted" -> "";
            case "time_to_win" -> "";
            case "time_to_win_formatted" -> "";
            default -> "";
        };
    }
}
