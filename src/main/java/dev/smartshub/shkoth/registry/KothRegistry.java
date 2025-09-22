package dev.smartshub.shkoth.registry;

import dev.smartshub.shkoth.api.event.koth.KothEndEvent;
import dev.smartshub.shkoth.api.koth.Koth;
import dev.smartshub.shkoth.api.team.track.TeamTracker;
import dev.smartshub.shkoth.loader.koth.KothLoader;
import dev.smartshub.shkoth.service.config.ConfigService;
import dev.smartshub.shkoth.service.schedule.KothSchedulerService;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class KothRegistry {

    private final ConfigService configService;
    private final KothLoader kothLoader;
    private final KothSchedulerService scheduler;

    private final Set<Koth> koths;

    public KothRegistry(ConfigService configService, TeamTracker teamHandler) {
        this.configService = configService;
        this.kothLoader = new KothLoader(configService, teamHandler);
        this.koths = kothLoader.load();
        scheduler = new KothSchedulerService(this, configService);
    }

    public void register(Koth koth) {
        koths.add(koth);
    }

    public void unregister(String kothId) {
        koths.removeIf(koth -> koth.getId().equalsIgnoreCase(kothId));
    }

    public void reloadKoths() {
        koths.clear();
        koths.addAll(kothLoader.load());
    }

    public Koth get(String id) {
        return koths.stream()
                .filter(koth -> koth.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    public Set<Koth> getAll() {
        return Set.copyOf(koths);
    }

    public Set<Koth> getRunning() {
        return koths.stream()
                .filter(Koth::isRunning)
                .collect(Collectors.toSet());
    }

    public Set<Koth> getScheduled() {
        return koths.stream()
                .filter(koth -> !koth.isRunning())
                .collect(Collectors.toSet());
    }

    public Koth getKothByPlayer(UUID playerID) {
        return koths.stream()
                .filter(koth -> koth.getPlayersInside().contains(playerID))
                .findFirst()
                .orElse(null);
    }

    public boolean startKoth(String kothId) {
        Koth koth = get(kothId);
        if (koth != null && !koth.isRunning()) {
            koth.start();
            return true;
        }
        return false;
    }

    public boolean stopKoth(String kothId) {
        Koth koth = get(kothId);
        if (koth != null && koth.isRunning()) {
            koth.stop(KothEndEvent.EndReason.MANUAL_STOP);
            return true;
        }
        return false;
    }

    public Duration getRemainingScheduleTime(String kothId) {
        Koth koth = get(kothId);

        if (koth != null && koth.isRunning()) {
            return Duration.ofSeconds(koth.getRemainingTime());
        }

        return scheduler.getTimeUntilNextSchedule(kothId);
    }

    public KothSchedulerService getScheduler() {
        return scheduler;
    }

    public String getNextKothToRun() {
        return scheduler.getNextKothToRun();
    }

    public long getTimeUntilNextKoth() {
        return scheduler.getSecondsUntilNextKothRuns();
    }

    public boolean isKothScheduledNow(String kothId) {
        return scheduler.isKothTime(kothId);
    }

    public long getTimeUntilNextSchedule(String kothId) {
        return scheduler.getTimeUntilNextSchedule(kothId).getSeconds();
    }

    public String getTimeUntilNextScheduleFormatted(String kothId) {
        return scheduler.getFormattedTimeUntilNext(kothId);
    }

    public String getTimeUntilScheduleEnds(String kothId) {
        return scheduler.getFormattedTimeUntilEnds(kothId);
    }


    public Duration getTimeUntilNext(String kothId) {
        return scheduler.getTimeUntilNextSchedule(kothId);
    }

}
