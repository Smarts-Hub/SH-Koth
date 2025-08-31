package dev.smartshub.shkoth.hook.placeholder;

import dev.smartshub.shkoth.api.stat.StatType;
import dev.smartshub.shkoth.registry.KothRegistry;
import dev.smartshub.shkoth.storage.cache.PlayerStatsCache;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final KothRegistry kothRegistry;
    private final PlayerStatsCache playerStatsCache;
    private final PlaceholderHelper placeholderHelper;

    public PlaceholderAPIHook(KothRegistry kothRegistry, PlayerStatsCache playerStatsCache) {
        this.kothRegistry = kothRegistry;
        this.playerStatsCache = playerStatsCache;
        this.placeholderHelper = new PlaceholderHelper(kothRegistry);
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

        if(kothName == null){
            return String.valueOf(switch (identifier) {
                case "player_context" -> tempArg1 != null ? tempArg1 : "";
                case "koth_context" -> tempArg2 != null ? tempArg2 : "";
                case "aux_context" -> tempArg3 != null ? tempArg3 : "";
                case "total_wins" -> playerStatsCache.getStat(player, StatType.TOTAL);
                case "solo_wins" -> playerStatsCache.getStat(player, StatType.SOLO);
                case "team_wins" -> playerStatsCache.getStat(player, StatType.TEAM);
                case "next_koth" -> kothRegistry.getNextKothToRun();
                case "next_koth_in" -> kothRegistry.getTimeUntilNextKoth();
                case "next_koth_in_formatted" -> placeholderHelper.formatTime(kothRegistry.getTimeUntilNextKoth());
                default -> "";
            });
        }

        // Related to a specific koth
        return String.valueOf(switch (placeholderType) {
            case "is_active" -> kothRegistry.get(kothName).isRunning() ? "true" : "false";
            case "is_scheduled" -> kothRegistry.isKothScheduledNow(kothName) ? "true" : "false";
            case "is_solo" -> kothRegistry.get(kothName).isSolo() ? "true" : "false";
            case "next_schedule" -> kothRegistry.getTimeUntilNextSchedule(kothName);
            case "next_schedule_formatted" -> kothRegistry.getTimeUntilNextScheduleFormatted(kothName);
            case "display_name" -> kothRegistry.get(kothName).getDisplayName();
            case "progress" -> kothRegistry.get(kothName).getCaptureProgress();
            case "id" -> kothName;
            case "world" -> kothRegistry.get(kothName).getArea().worldName();
            case "x" -> kothRegistry.get(kothName).getArea().getCenter().getBlockX();
            case "y" -> kothRegistry.get(kothName).getArea().getCenter().getBlockY();
            case "z" -> kothRegistry.get(kothName).getArea().getCenter().getBlockZ();
            case "is_running" -> kothRegistry.get(kothName).isRunning();
            case "is_capturing" -> kothRegistry.get(kothName).isCapturing();
            case "capturer" -> kothRegistry.get(kothName).getCurrentCapturerPlayer().getName();
            case "captured" -> kothRegistry.get(kothName).getCaptureTime();
            case "captured_formatted" -> placeholderHelper.formatTime(kothRegistry.get(kothName).getCaptureTime());
            case "max_time" -> kothRegistry.get(kothName).getDuration();
            case "max_time_formatted" -> placeholderHelper.formatTime(kothRegistry.get(kothName).getDuration());
            case "time_left" -> kothRegistry.getRemainingScheduleTime(kothName).getSeconds();
            case "time_left_formatted" -> placeholderHelper.formatTime(kothRegistry.getRemainingScheduleTime(kothName).getSeconds());
            case "time_to_win" -> kothRegistry.get(kothName).getSecondsUntilCaptureComplete();
            case "time_to_win_formatted" -> placeholderHelper.formatTime(kothRegistry.get(kothName).getSecondsUntilCaptureComplete());
            default -> "";
        });
    }
}
