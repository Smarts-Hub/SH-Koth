package dev.smartshub.shkoth.service.schedule;

import dev.smartshub.shkoth.api.schedule.Schedule;
import dev.smartshub.shkoth.api.schedule.ScheduleStatus;

import java.time.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class SchedulerManagementService {

    private final String kothId;
    private final List<Schedule> schedules;
    private final Duration duration;
    private final TimeService timeService;
    private boolean wasActiveTime = false;

    public SchedulerManagementService(String kothId, List<Schedule> schedules, Duration duration, TimeService timeService) {
        this.kothId = kothId;
        this.schedules = new ArrayList<>(schedules);
        this.duration = duration;
        this.timeService = timeService;
        sortSchedules();
    }

    private void sortSchedules() {
        schedules.sort(Comparator.comparing(Schedule::day).thenComparing(Schedule::time));
    }

    public boolean isActiveTime() {
        LocalDateTime now = timeService.getCurrentDateTime();
        DayOfWeek currentDay = now.getDayOfWeek();
        LocalTime currentTime = now.toLocalTime();

        return schedules.stream().anyMatch(schedule ->
                schedule.day().equals(currentDay) &&
                        !currentTime.isBefore(schedule.time()) &&
                        currentTime.isBefore(schedule.time().plus(duration))
        );
    }

    public ScheduleStatus checkStatusChange() {
        boolean nowActive = isActiveTime();

        if (!wasActiveTime && nowActive) {
            wasActiveTime = true;
            return ScheduleStatus.STARTED;
        }

        if (wasActiveTime && !nowActive) {
            wasActiveTime = false;
            return ScheduleStatus.ENDED;
        }

        return ScheduleStatus.NO_CHANGE;
    }

    public Duration getTimeUntilNext() {
        if (isActiveTime()) return Duration.ZERO;

        LocalDateTime now = timeService.getCurrentDateTime();
        Schedule nextSchedule = findNextSchedule(now);

        if (nextSchedule == null) return Duration.ZERO;

        LocalDateTime nextDateTime = calculateNextDateTime(nextSchedule, now);
        return Duration.between(now, nextDateTime);
    }

    public Duration getTimeUntilEnds() {
        if (!isActiveTime()) return Duration.ZERO;
        return duration.minus(getTimeSinceStarted());
    }

    public Duration getTimeSinceStarted() {
        if (!isActiveTime()) return Duration.ZERO;

        LocalDateTime now = timeService.getCurrentDateTime();
        Schedule currentSchedule = findCurrentSchedule(now);

        if (currentSchedule == null) return Duration.ZERO;

        LocalDateTime scheduleStart = calculateScheduleDateTime(currentSchedule, now);
        return Duration.between(scheduleStart, now);
    }

    public LocalTime getNextScheduleTime() {
        LocalDateTime now = timeService.getCurrentDateTime();
        Schedule nextSchedule = findNextSchedule(now);
        return nextSchedule != null ? nextSchedule.time() : null;
    }

    private Schedule findNextSchedule(LocalDateTime now) {
        DayOfWeek currentDay = now.getDayOfWeek();
        LocalTime currentTime = now.toLocalTime();

        // The schedule for today
        for (Schedule schedule : schedules) {
            if (schedule.day().equals(currentDay) && currentTime.isBefore(schedule.time())) {
                return schedule;
            }
        }

        // Schedules for the next 7 days
        for (int i = 1; i <= 7; i++) {
            DayOfWeek targetDay = currentDay.plus(i);
            for (Schedule schedule : schedules) {
                if (schedule.day().equals(targetDay)) {
                    return schedule;
                }
            }
        }

        return null;
    }

    private Schedule findCurrentSchedule(LocalDateTime now) {
        DayOfWeek currentDay = now.getDayOfWeek();
        LocalTime currentTime = now.toLocalTime();

        return schedules.stream()
                .filter(schedule ->
                        schedule.day().equals(currentDay) &&
                                !currentTime.isBefore(schedule.time()) &&
                                currentTime.isBefore(schedule.time().plus(duration)))
                .findFirst()
                .orElse(null);
    }

    private LocalDateTime calculateNextDateTime(Schedule schedule, LocalDateTime from) {
        DayOfWeek targetDay = schedule.day();
        DayOfWeek currentDay = from.getDayOfWeek();

        int daysUntil = targetDay.getValue() - currentDay.getValue();
        if (daysUntil <= 0) daysUntil += 7;

        return from.toLocalDate().plusDays(daysUntil).atTime(schedule.time());
    }

    private LocalDateTime calculateScheduleDateTime(Schedule schedule, LocalDateTime reference) {
        DayOfWeek targetDay = schedule.day();
        LocalDate targetDate = reference.toLocalDate();

        if (reference.getDayOfWeek() != targetDay) {
            int daysBack = reference.getDayOfWeek().getValue() - targetDay.getValue();
            if (daysBack < 0) daysBack += 7;
            targetDate = targetDate.minusDays(daysBack);
        }

        return targetDate.atTime(schedule.time());
    }

    public String getKothId() {
        return kothId;
    }

    public List<Schedule> getSchedules() {
        return List.copyOf(schedules);
    }

    public Duration getDuration() {
        return duration;
    }
}


