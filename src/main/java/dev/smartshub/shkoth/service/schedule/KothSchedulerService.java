package dev.smartshub.shkoth.service.schedule;

import dev.smartshub.shkoth.api.koth.Koth;
import dev.smartshub.shkoth.api.schedule.Schedule;
import dev.smartshub.shkoth.api.schedule.ScheduleStatus;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class KothSchedulerService {

    private final Map<String, SchedulerManagementService> scheduleManagers;
    private final TimeService timeService;

    public KothSchedulerService(TimeService timeService) {
        this.timeService = timeService;
        this.scheduleManagers = new ConcurrentHashMap<>();
    }

    public KothSchedulerService() {
        this(new TimeService());
    }

    public void initializeFromKoths(Set<Koth> koths) {
        scheduleManagers.clear();

        for (Koth koth : koths) {
            List<Schedule> schedules = koth.getSchedules();
            if (!schedules.isEmpty()) {
                Duration duration = Duration.ofSeconds(koth.getDuration());
                addKothSchedule(koth.getId(), schedules, duration);
            }
        }
    }

    public void addKothSchedule(String kothId, List<Schedule> schedules, Duration duration) {
        if (schedules == null || schedules.isEmpty()) return;

        SchedulerManagementService manager = new SchedulerManagementService(kothId, schedules, duration, timeService);
        scheduleManagers.put(kothId, manager);
    }

    public void removeKothSchedule(String kothId) {
        scheduleManagers.remove(kothId);
    }

    public boolean isKothTime(String kothId) {
        SchedulerManagementService manager = scheduleManagers.get(kothId);
        return manager != null && manager.isActiveTime();
    }

    public ScheduleStatus updateAndCheckStatusChange(String kothId) {
        SchedulerManagementService manager = scheduleManagers.get(kothId);
        return manager != null ? manager.checkStatusChange() : ScheduleStatus.NO_CHANGE;
    }

    public Duration getTimeUntilNextSchedule(String kothId) {
        SchedulerManagementService manager = scheduleManagers.get(kothId);
        return manager != null ? manager.getTimeUntilNext() : Duration.ZERO;
    }

    public Duration getTimeUntilScheduleEnds(String kothId) {
        SchedulerManagementService manager = scheduleManagers.get(kothId);
        return manager != null ? manager.getTimeUntilEnds() : Duration.ZERO;
    }

    public Duration getTimeSinceScheduleStarted(String kothId) {
        SchedulerManagementService manager = scheduleManagers.get(kothId);
        return manager != null ? manager.getTimeSinceStarted() : Duration.ZERO;
    }

    public LocalTime getNextScheduleTime(String kothId) {
        SchedulerManagementService manager = scheduleManagers.get(kothId);
        return manager != null ? manager.getNextScheduleTime() : null;
    }

    public String getFormattedTimeUntilNext(String kothId) {
        return timeService.formatDuration(getTimeUntilNextSchedule(kothId));
    }

    public String getFormattedTimeUntilEnds(String kothId) {
        if (!isKothTime(kothId)) return "";
        return timeService.formatDuration(getTimeUntilScheduleEnds(kothId));
    }

    public String getFormattedNextScheduleTime(String kothId) {
        LocalTime nextTime = getNextScheduleTime(kothId);
        return nextTime != null ? timeService.formatTime(nextTime) : "";
    }

    public Set<String> getAllScheduledKoths() {
        return Set.copyOf(scheduleManagers.keySet());
    }

    public Set<String> getActiveKoths() {
        return scheduleManagers.entrySet().stream()
                .filter(entry -> entry.getValue().isActiveTime())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public Map<String, ScheduleStatus> processAllSchedules() {
        return scheduleManagers.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().checkStatusChange()
                ));
    }

    public boolean hasSchedule(String kothId) {
        return scheduleManagers.containsKey(kothId);
    }

    public SchedulerManagementService getScheduleManager(String kothId) {
        return scheduleManagers.get(kothId);
    }

    public TimeService getTimeService() {
        return timeService;
    }
}
