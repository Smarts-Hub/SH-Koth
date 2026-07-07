package dev.smartshub.shkoth.service.schedule;

import dev.smartshub.shkoth.api.config.ConfigType;
import dev.smartshub.shkoth.api.koth.Koth;
import dev.smartshub.shkoth.api.schedule.Scheduler;
import dev.smartshub.shkoth.builder.mapper.SchedulerConfigMapper;
import dev.smartshub.shkoth.registry.KothRegistry;
import dev.smartshub.shkoth.service.config.ConfigService;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class KothScheduler {

    private final KothRegistry kothRegistry;
    private final ConfigService configService;
    private final List<Scheduler> schedulers = new ArrayList<>();
    private final Map<String, Long> lastExecutionTimes = new HashMap<>();

    public KothScheduler(KothRegistry kothRegistry, ConfigService configService) {
        this.kothRegistry = kothRegistry;
        this.configService = configService;
        reload();
    }

    public void reload() {
        SchedulerConfigMapper mapper = new SchedulerConfigMapper();
        schedulers.clear();
        schedulers.addAll(mapper.map(configService.provide(ConfigType.SCHEDULERS)));
        lastExecutionTimes.clear();
    }

    public boolean isKothScheduled(String kothId) {
        return getScheduledKothIds().contains(kothId);
    }

    public Set<Koth> getScheduled() {
        return kothRegistry.getAll().stream()
                .filter(koth -> isKothScheduled(koth.getId()))
                .collect(Collectors.toSet());
    }

    public Set<String> getScheduledKothIds() {
        return schedulers.stream()
                .flatMap(scheduler -> scheduler.kothIds().stream())
                .collect(Collectors.toSet());
    }

    // Timestamp helpoer
    private long getNextExecutionTimestamp(Predicate<Scheduler> schedulerFilter) {
        long currentTime = System.currentTimeMillis();

        return schedulers.stream()
                .filter(schedulerFilter) // Filtra el scheduler antes de evaluar crons
                .flatMap(scheduler -> scheduler.cronExpressions().stream()
                        .map(cron -> scheduler.createPredictor(cron).nextMatchingTime()))
                .filter(nextTime -> nextTime > currentTime)
                .min(Long::compare)
                .orElse(-1L);
    }

    public long getTimeUntilNextSchedule(String kothId) {
        if (!isKothScheduled(kothId)) return -1L;

        long nextTime = getNextExecutionTimestamp(scheduler -> scheduler.kothIds().contains(kothId));
        return Duration.ofMillis(nextTime - System.currentTimeMillis()).getSeconds();
    }

    public long getSecondsUntilNextKothRuns() {
        long nextTime = getNextExecutionTimestamp(scheduler -> true); // true = no filtra ningún scheduler
        return Duration.ofMillis(nextTime - System.currentTimeMillis()).getSeconds();
    }

    public String getNextKothToRun() {
        long currentTime = System.currentTimeMillis();

        // Estructura interna temporal ligera para no perder la referencia del Scheduler mapeado al buscar el mínimo
        record TemporaryPrediction(Scheduler scheduler, long nextTime) {
        }

        return schedulers.stream()
                .flatMap(scheduler -> scheduler.cronExpressions().stream()
                        .map(cron -> new TemporaryPrediction(scheduler, scheduler.createPredictor(cron).nextMatchingTime())))
                .filter(pred -> pred.nextTime() > currentTime)
                .min(Comparator.comparingLong(TemporaryPrediction::nextTime))
                .map(pred -> resolveKothIdForSchedule(pred.scheduler(), pred.nextTime()))
                .orElse(null);
    }

    // TODO: Might take a look into this later.
    public void processAllSchedulersAndExecute() {
        long currentTime = System.currentTimeMillis();
        long minuteStart = currentTime - (currentTime % 60_000L);

        for (Scheduler scheduler : schedulers) {
            for (String cronExpression : scheduler.cronExpressions()) {
                if (!scheduler.matches(cronExpression, minuteStart)) continue;

                String executionKey = createExecutionKey(scheduler, cronExpression);

                Long lastExecution = lastExecutionTimes.get(executionKey);
                if (lastExecution != null && lastExecution >= minuteStart) continue;

                if (scheduler.random()) {
                    String kothId = resolveKothIdForSchedule(scheduler, minuteStart);
                    if (kothId == null) continue;

                    if (handleScheduledKoth(kothId)) {
                        lastExecutionTimes.put(executionKey, minuteStart);
                    }
                } else {
                    boolean anyHandled = false;
                    for (String id : scheduler.kothIds()) {
                        if (handleScheduledKoth(id)) {
                            anyHandled = true;
                        }
                    }
                    if (anyHandled) {
                        lastExecutionTimes.put(executionKey, minuteStart);
                    }
                }
            }
        }

        cleanOldExecutions();
    }

    private boolean handleScheduledKoth(String kothId) {
        var koth = kothRegistry.get(kothId);
        if (koth == null) {
            JavaPlugin.getProvidingPlugin(this.getClass()).getLogger().severe(
                    "Can't start the koth scheduled. ID '" + kothId + "' does not exists in the registry.");
            return true;
        }
        return koth.isRunning() || kothRegistry.startKoth(kothId);
    }

    private String resolveKothIdForSchedule(Scheduler scheduler, long scheduleTime) {
        List<String> kothIds = scheduler.kothIds();
        if (kothIds.isEmpty()) return null;

        return scheduler.random()
                ? kothIds.get(Math.floorMod(scheduleTime / 60_000L, kothIds.size()))
                : kothIds.getFirst();
    }

    private String createExecutionKey(Scheduler scheduler, String cronExpression) {
        return scheduler.id() + "_" + cronExpression;
    }

    public void cleanOldExecutions() {
        long currentTime = System.currentTimeMillis();
        long threshold = 3_600_000L;
        lastExecutionTimes.entrySet().removeIf(entry -> currentTime - entry.getValue() > threshold);
    }
}