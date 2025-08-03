package dev.smartshub.shkoth.api.model.time;

import java.time.LocalTime;

public record Schedule(
        Day day,
        LocalTime time
) {
}
