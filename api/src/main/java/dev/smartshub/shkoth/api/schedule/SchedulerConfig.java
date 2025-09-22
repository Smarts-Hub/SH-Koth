package dev.smartshub.shkoth.api.schedule;

import java.util.List;
import java.util.TimeZone;

public record SchedulerConfig(
        String id,
        List<String> cronExpressions,
        TimeZone timeZone,
        boolean random,
        List<String> kothIds
) {
    public static SchedulerConfig of(String id, List<String> cronExpressions, String timeZoneId, boolean random, List<String> kothIds) {
        TimeZone tz = timeZoneId.equalsIgnoreCase("server") ?
                TimeZone.getDefault() :
                TimeZone.getTimeZone(timeZoneId);
        return new SchedulerConfig(id, cronExpressions, tz, random, kothIds);
    }
}