package dev.smartshub.shkoth.api.location.schedule;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record Schedule(
        DayOfWeek day,
        LocalTime time
) {
}
