package dev.smartshub.shkoth.service.schedule;

import dev.smartshub.shkoth.api.config.ConfigType;
import dev.smartshub.shkoth.api.schedule.Scheduler;
import dev.smartshub.shkoth.builder.mapper.SchedulerConfigMapper;
import dev.smartshub.shkoth.registry.KothRegistry;
import dev.smartshub.shkoth.service.config.ConfigService;

import java.time.Duration;
import java.util.List;

public class KothScheduler {

    private final TimeService timeService = new TimeService();
    private final KothRegistry kothRegistry;
    private final List<Scheduler> schedulers;

    public KothScheduler(KothRegistry kothRegistry, ConfigService configService) {
        this.kothRegistry = kothRegistry;
        SchedulerConfigMapper mapper = new SchedulerConfigMapper();
        this.schedulers = mapper.map(configService.provide(ConfigType.SCHEDULERS));
    }

    public Duration getTimeUntilNextSchedule(String kothId) {
        Duration shortest = null;
        long currentTime = System.currentTimeMillis();

        for (Scheduler scheduler : schedulers) {
            if (scheduler.kothIds().contains(kothId)) {
                for (String cronExpression : scheduler.cronExpressions()) {
                    var predictor = scheduler.createPredictor(cronExpression);
                    long nextTime = predictor.nextMatchingTime();
                    Duration duration = Duration.ofMillis(nextTime - currentTime);

                    if (duration.isNegative()) continue;

                    if (shortest == null || duration.compareTo(shortest) < 0) {
                        shortest = duration;
                    }
                }
            }
        }

        return shortest;
    }

    public String getNextKothToRun() {
        String nextKoth = null;
        long shortestTime = Long.MAX_VALUE;
        long currentTime = System.currentTimeMillis();

        for (Scheduler scheduler : schedulers) {
            for (String cronExpression : scheduler.cronExpressions()) {
                var predictor = scheduler.createPredictor(cronExpression);
                long nextTime = predictor.nextMatchingTime();

                if (nextTime <= currentTime) continue;

                if (nextTime < shortestTime) {
                    shortestTime = nextTime;
                    if (scheduler.random()) {
                        int randomIndex = (int) (Math.random() * scheduler.kothIds().size());
                        nextKoth = scheduler.kothIds().get(randomIndex);
                    } else {
                        nextKoth = scheduler.kothIds().getFirst();
                    }
                }
            }
        }

        return nextKoth;
    }

    public long getSecondsUntilNextKothRuns() {
        long shortestTime = Long.MAX_VALUE;
        long currentTime = System.currentTimeMillis();

        for (Scheduler scheduler : schedulers) {
            for (String cronExpression : scheduler.cronExpressions()) {
                var predictor = scheduler.createPredictor(cronExpression);
                long nextTime = predictor.nextMatchingTime();

                if (nextTime <= currentTime) continue;

                if (nextTime < shortestTime) {
                    shortestTime = nextTime;
                }
            }
        }

        if (shortestTime == Long.MAX_VALUE) return -1;

        return (shortestTime - currentTime) / 1000;
    }

    public String getFormattedTimeUntilNext(String kothId) {
        Duration duration = getTimeUntilNextSchedule(kothId);
        if (duration == null) return "N/A";
        return timeService.formatDuration(duration);
    }

    public void processAllSchedulersAndExecute() {
        long currentTime = System.currentTimeMillis();

        for (Scheduler scheduler : schedulers) {
            for (String cronExpression : scheduler.cronExpressions()) {
                var predictor = scheduler.createPredictor(cronExpression);
                long nextTime = predictor.nextMatchingTime();

                if (Math.abs(nextTime - currentTime) > 60000) continue;

                if (scheduler.random()) {
                    int randomIndex = (int) (Math.random() * scheduler.kothIds().size());
                    String kothId = scheduler.kothIds().get(randomIndex);
                    kothRegistry.get(kothId).start();
                } else {
                    scheduler.kothIds().forEach(id -> kothRegistry.get(id).start());
                }
            }
        }
    }
}