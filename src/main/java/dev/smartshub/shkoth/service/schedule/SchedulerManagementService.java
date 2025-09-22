package dev.smartshub.shkoth.service.schedule;

import dev.smartshub.shkoth.api.schedule.ScheduleStatus;
import dev.smartshub.shkoth.api.schedule.SchedulerConfig;
import dev.smartshub.shkoth.registry.KothRegistry;
import it.sauronsoftware.cron4j.Predictor;
import it.sauronsoftware.cron4j.SchedulingPattern;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class SchedulerManagementService {

    private final SchedulerConfig config;
    private final List<SchedulingPattern> patterns;
    private boolean wasActiveTime = false;

    private String activeRandomKoth = null;

    public SchedulerManagementService(SchedulerConfig config) {
        this.config = config;
        this.patterns = config.cronExpressions().stream()
                .map(SchedulingPattern::new)
                .toList();
    }

    public boolean isActiveTime(KothRegistry kothRegistry) {
        long currentTime = System.currentTimeMillis();
        long lastExecution = getLastMatchingTimeFromAll();
        return config.kothIds().stream().anyMatch(kothId -> {
            long kothDurationMillis = kothRegistry.get(kothId).getDuration();
            return currentTime >= lastExecution && currentTime < (lastExecution + kothDurationMillis);
        });
    }

    public ScheduleStatus checkStatusChange(KothRegistry kothRegistry) {
        boolean nowActive = isActiveTime(kothRegistry);
        if (!wasActiveTime && nowActive) {
            wasActiveTime = true;
            return ScheduleStatus.STARTED;
        }
        if (wasActiveTime && !nowActive) {
            wasActiveTime = false;
            resetRandomSelection();
            return ScheduleStatus.ENDED;
        }
        return ScheduleStatus.NO_CHANGE;
    }

    public Duration getTimeUntilNext() {
        long currentTime = System.currentTimeMillis();
        long nextExecution = getNextMatchingTimeFromAll();
        if (nextExecution <= currentTime) return Duration.ZERO;
        return Duration.ofMillis(nextExecution - currentTime);
    }

    public LocalTime getNextScheduleTime() {
        long nextTime = getNextMatchingTimeFromAll();
        if (nextTime <= 0) return null;
        return Instant.ofEpochMilli(nextTime).atZone(config.timeZone().toZoneId()).toLocalTime();
    }

    public List<String> getKothsToExecute() {
        if (config.random() && !config.kothIds().isEmpty()) {
            if (activeRandomKoth == null) {
                int index = ThreadLocalRandom.current().nextInt(config.kothIds().size());
                activeRandomKoth = config.kothIds().get(index);
            }
            return List.of(activeRandomKoth);
        }
        return config.kothIds();
    }

    public void resetRandomSelection() {
        activeRandomKoth = null;
    }

    private long getNextMatchingTime(String cronExpression) {
        Predictor predictor = new Predictor(cronExpression);
        predictor.setTimeZone(config.timeZone());
        return predictor.nextMatchingTime() * 1000L;
    }

    private long getLastMatchingTime(SchedulingPattern pattern) {
        long current = System.currentTimeMillis();
        long step = 60 * 1000;
        for (long time = current; time > current - (24 * 60 * 60 * 1000); time -= step) {
            if (pattern.match(time)) {
                return findExactMatch(pattern, time, time + step);
            }
        }
        return 0;
    }

    private long findExactMatch(SchedulingPattern pattern, long start, long end) {
        while (end - start > 1000) {
            long mid = (start + end) / 2;
            if (pattern.match(mid)) {
                start = mid;
            } else {
                end = mid;
            }
        }
        return start;
    }

    private long getNextMatchingTimeFromAll() {
        return config.cronExpressions().stream()
                .mapToLong(this::getNextMatchingTime)
                .min()
                .orElse(0L);
    }

    private long getLastMatchingTimeFromAll() {
        return patterns.stream()
                .mapToLong(this::getLastMatchingTime)
                .max()
                .orElse(0L);
    }

    public SchedulerConfig getConfig() {
        return config;
    }
}
