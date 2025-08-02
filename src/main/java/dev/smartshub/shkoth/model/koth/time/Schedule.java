package dev.smartshub.shkoth.model.koth.time;

import java.time.LocalTime;

public record Schedule(
        Day day,
        LocalTime time
) {
}
