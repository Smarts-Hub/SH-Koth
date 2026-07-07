package dev.smartshub.shkoth.api.schedule;

import it.sauronsoftware.cron4j.Predictor;
import it.sauronsoftware.cron4j.SchedulingPattern;

import java.util.List;
import java.util.TimeZone;

public record Scheduler(
        String id,
        List<String> cronExpressions,
        TimeZone timeZone,
        boolean random,
        List<String> kothIds
) {
    public Predictor createPredictor(String cronExpression) {
        Predictor predictor = new Predictor(cronExpression);
        predictor.setTimeZone(timeZone);
        return predictor;
    }

    public SchedulingPattern createSchedulingPattern(String cronExpression) {
        return new SchedulingPattern(cronExpression);
    }

    public boolean matches(String cronExpression, long millis) {
        return createSchedulingPattern(cronExpression).match(timeZone, millis);
    }

}