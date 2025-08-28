package dev.smartshub.shkoth.api.location.schedule;

import java.time.Duration;
import java.time.LocalTime;

public interface KothScheduler {
    boolean isKothTime(String kothId);
    ScheduleStatus updateAndCheckStatusChange(String kothId);
    Duration getTimeUntilNextSchedule(String kothId);
    Duration getTimeUntilScheduleEnds(String kothId);
    Duration getTimeSinceScheduleStarted(String kothId);
    LocalTime getNextScheduleTime(String kothId);
    String getFormattedTimeUntilNext(String kothId);
    String getFormattedTimeUntilEnds(String kothId);
    String getFormattedNextScheduleTime(String kothId);
}