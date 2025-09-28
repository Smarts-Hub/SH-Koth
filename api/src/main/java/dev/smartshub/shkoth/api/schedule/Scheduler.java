package dev.smartshub.shkoth.api.schedule;

import it.sauronsoftware.cron4j.Predictor;

import java.util.List;
import java.util.TimeZone;

public record Scheduler(
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

    public List<Predictor> getPredictors() {
        return cronExpressions.stream()
                .map(this::createPredictor)
                .toList();
    }
}