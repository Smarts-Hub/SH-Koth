package dev.smartshub.shkoth.service.schedule;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class TimeService {

    public LocalTime getCurrentTime() {
        return LocalTime.now(ZoneId.systemDefault());
    }

    public LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now(ZoneId.systemDefault());
    }

    public DayOfWeek getCurrentDay() {
        return getCurrentDateTime().getDayOfWeek();
    }

    public String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%d:%02d", minutes, seconds);
        } else {
            return String.valueOf(seconds);
        }
    }

    public String formatTime(LocalTime time) {
        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}
