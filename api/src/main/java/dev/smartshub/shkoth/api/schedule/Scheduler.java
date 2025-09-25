package dev.smartshub.shkoth.api.schedule;

import it.sauronsoftware.cron4j.Predictor;

import java.util.List;

public record Scheduler(
        List<Predictor> cronExpressions,
        boolean random,
        List<String> kothIds
) {}