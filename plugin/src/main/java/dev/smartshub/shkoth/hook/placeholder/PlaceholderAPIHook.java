package dev.smartshub.shkoth.hook.placeholder;

import dev.smartshub.shkoth.api.koth.Koth;
import dev.smartshub.shkoth.api.stat.StatType;
import dev.smartshub.shkoth.registry.KothRegistry;
import dev.smartshub.shkoth.service.schedule.KothScheduler;
import dev.smartshub.shkoth.storage.cache.PlayerStatsCache;
import dev.smartshub.shkoth.storage.cache.PushStackCache;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final KothScheduler scheduler;
    private final KothRegistry kothRegistry;
    private final PlayerStatsCache playerStatsCache;
    private final PlaceholderHelper placeholderHelper;

    public PlaceholderAPIHook(KothScheduler scheduler, KothRegistry kothRegistry, PlayerStatsCache playerStatsCache) {
        this.scheduler = scheduler;
        this.kothRegistry = kothRegistry;
        this.playerStatsCache = playerStatsCache;
        this.placeholderHelper = new PlaceholderHelper(kothRegistry);
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
        return "1.0.1";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        String kothName = placeholderHelper.extractKothName(identifier);
        String placeholderType = placeholderHelper.extractPlaceholderType(identifier);

        if (kothName == null) {
            return String.valueOf(switch (identifier) {
                case "total_wins" -> playerStatsCache.getStat(player, StatType.TOTAL);
                case "solo_wins" -> playerStatsCache.getStat(player, StatType.SOLO);
                case "team_wins" -> playerStatsCache.getStat(player, StatType.TEAM);
                case "next_koth" -> scheduler.getNextKothToRun();
                case "next_koth_in" -> scheduler.getSecondsUntilNextKothRuns();
                case "next_koth_in_formatted" -> placeholderHelper.formatTime(scheduler.getSecondsUntilNextKothRuns());
                case "latest_winner" -> PushStackCache.getArg4();
                default -> "";
            });
        }

        // Early return if koth is null
        Koth koth = kothRegistry.get(kothName);
        if (koth == null) return "";

        // Related to a specific koth
        return String.valueOf(switch (placeholderType) {
            case "id" -> kothName;
            case "display_name" -> koth.getDisplayName();
            case "world" -> koth.getArea().worldName();
            case "x" -> koth.getArea().getCenter().getBlockX();
            case "y" -> koth.getArea().getCenter().getBlockY();
            case "z" -> koth.getArea().getCenter().getBlockZ();

            // Status & cap related
            case "is_solo" -> koth.isSolo();
            case "is_running" -> koth.isRunning();
            case "is_active" -> koth.isRunning() && koth.isCapturing();
            case "progress" -> koth.getCaptureProgress();
            case "is_capturing" -> koth.isCapturing();
            case "inside" -> (player != null) && koth.getArea().contains(player.getLocation());
            case "inside_count" -> koth.getPlayersInside().size();
            case "capturer" ->
                    (koth.getCurrentCapturerPlayer() == null) ? "" : koth.getCurrentCapturerPlayer().getName();
            case "captured" -> koth.getCaptureTime();
            case "captured_formatted" -> placeholderHelper.formatTime(koth.getCaptureTime());

            // Time related
            case "is_scheduled" -> scheduler.isKothScheduled(kothName);
            case "next_schedule" -> scheduler.getTimeUntilNextSchedule(kothName);
            case "next_schedule_formatted" ->
                    placeholderHelper.formatTime(scheduler.getTimeUntilNextSchedule(kothName));
            case "max_time" -> koth.getDuration();
            case "max_time_formatted" -> placeholderHelper.formatTime(koth.getDuration());
            case "time_left" -> koth.getRemainingTime();
            case "time_left_formatted" -> placeholderHelper.formatTime(koth.getRemainingTime());
            case "time_to_win" -> koth.getSecondsUntilCaptureComplete();
            case "time_to_win_formatted" -> placeholderHelper.formatTime(koth.getSecondsUntilCaptureComplete());

            default -> "";
        });
    }
}
