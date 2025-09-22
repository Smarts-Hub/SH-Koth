package dev.smartshub.shkoth.service.schedule;

import dev.smartshub.shkoth.api.schedule.ScheduleStatus;
import dev.smartshub.shkoth.api.schedule.SchedulerConfig;
import dev.smartshub.shkoth.loader.scheduler.SchedulerLoader;
import dev.smartshub.shkoth.registry.KothRegistry;
import dev.smartshub.shkoth.service.config.ConfigService;

import java.time.Duration;
import java.util.*;

public class KothSchedulerService {

    private final List<SchedulerManagementService> schedulerManagers;
    private final KothRegistry kothRegistry;
    private final SchedulerLoader schedulerLoader;
    private final TimeService timeService = new TimeService();

    public KothSchedulerService(KothRegistry kothRegistry, ConfigService configService) {
        this.kothRegistry = kothRegistry;
        this.schedulerLoader = new SchedulerLoader(configService);
        this.schedulerManagers = new ArrayList<>();
        initializeSchedulers();
    }

    private void initializeSchedulers() {
        List<SchedulerConfig> configs = schedulerLoader.load();
        for (SchedulerConfig config : configs) {
            schedulerManagers.add(new SchedulerManagementService(config));
        }
    }

    public boolean isKothTime(String kothId) {
        return schedulerManagers.stream()
                .anyMatch(manager -> manager.getConfig().kothIds().contains(kothId) && manager.isActiveTime(kothRegistry));
    }

    public Duration getTimeUntilNextSchedule(String kothId) {
        return schedulerManagers.stream()
                .filter(manager -> manager.getConfig().kothIds().contains(kothId))
                .map(SchedulerManagementService::getTimeUntilNext)
                .min(Duration::compareTo)
                .orElse(Duration.ZERO);
    }

    public void processAllSchedulersAndExecute() {
        for (SchedulerManagementService manager : schedulerManagers) {
            ScheduleStatus status = manager.checkStatusChange(kothRegistry);
            switch (status) {
                case STARTED -> handleSchedulerStart(manager);
                case ENDED -> handleSchedulerEnd(manager);
                case NO_CHANGE -> {}
            }
        }
    }

    private void handleSchedulerStart(SchedulerManagementService manager) {
        List<String> kothsToStart = manager.getKothsToExecute();
        for (String kothId : kothsToStart) {
            kothRegistry.startKoth(kothId);
        }
    }

    private void handleSchedulerEnd(SchedulerManagementService manager) {
        List<String> kothsToStop = manager.getConfig().kothIds().stream()
                .filter(kothId -> kothRegistry.getRunning().stream()
                        .anyMatch(koth -> koth.getId().equals(kothId)))
                .toList();

        for (String kothId : kothsToStop) {
            kothRegistry.stopKoth(kothId);
        }
    }

    public String getFormattedTimeUntilNext(String kothId) {
        return timeService.formatDuration(getTimeUntilNextSchedule(kothId));
    }

    public Set<String> getActiveKoths() {
        Set<String> activeKoths = new HashSet<>();

        schedulerManagers.stream()
                .filter(manager -> manager.isActiveTime(kothRegistry))
                .flatMap(manager -> manager.getConfig().kothIds().stream())
                .forEach(activeKoths::add);

        kothRegistry.getRunning().forEach(koth -> activeKoths.add(koth.getId()));

        return activeKoths;
    }

    public boolean hasScheduleForKoth(String kothId) {
        return schedulerManagers.stream()
                .anyMatch(manager -> manager.getConfig().kothIds().contains(kothId));
    }

    public List<SchedulerConfig> getSchedulerConfigs() {
        List<SchedulerConfig> configs = new ArrayList<>();
        for (SchedulerManagementService manager : schedulerManagers) {
            configs.add(manager.getConfig());
        }
        return configs;
    }

    public String getNextKothToRun(){
        return schedulerManagers.stream()
                .map(manager -> {
                    Duration timeUntilNext = manager.getTimeUntilNext();
                    return new AbstractMap.SimpleEntry<>(manager.getConfig().kothIds(), timeUntilNext);
                })
                .filter(entry -> !entry.getValue().isZero())
                .min(Comparator.comparing(AbstractMap.SimpleEntry::getValue))
                .map(AbstractMap.SimpleEntry::getKey)
                .flatMap(kothIds -> kothIds.stream().findFirst())
                .orElse("N/A");
    }

    public long getSecondsUntilNextKothRuns(){
        return schedulerManagers.stream()
                .map(SchedulerManagementService::getTimeUntilNext)
                .min(Duration::compareTo)
                .orElse(Duration.ZERO)
                .getSeconds();
    }

    public long getTimeUntilKothEnds(String kothId) {
        return kothRegistry.getRemainingScheduleTime(kothId).getSeconds();
    }

    public String getFormattedTimeUntilEnds(String kothId) {
        return timeService.formatDuration(kothRegistry.getRemainingScheduleTime(kothId));
    }
}
