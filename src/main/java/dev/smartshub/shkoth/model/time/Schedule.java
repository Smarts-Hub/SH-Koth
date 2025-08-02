package dev.smartshub.shkoth.model.time;

import java.time.LocalTime;

public record Schedule(
        Day day,
        LocalTime time
) {
}
