package dev.smartshub.shkoth.builder.mapper;

import dev.smartshub.shkoth.api.builder.mapper.Mapper;
import dev.smartshub.shkoth.api.config.ConfigContainer;
import dev.smartshub.shkoth.api.schedule.Scheduler;

import java.util.*;

public class SchedulerConfigMapper implements Mapper<List<Scheduler>, ConfigContainer> {

    @Override
    public List<Scheduler> map(ConfigContainer config) {
        String tz = config.getString("time-zone", "server");
        TimeZone timeZone = TimeZone.getTimeZone(tz);
        Set<String> schedulerIds = config.getKeys("schedulers");
        List<Scheduler> result = new ArrayList<>();

        for (String schedulerId : schedulerIds) {
            String base = "schedulers." + schedulerId;
            List<String> times = config.getStringList(base + ".times", Collections.emptyList());
            if (times == null || times.isEmpty()) continue;

            List<String> validTimes = times.stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
            if (validTimes.isEmpty()) continue;

            List<String> koths = config.getStringList(base + ".koths", Collections.emptyList());
            if (koths == null || koths.isEmpty()) continue;

            boolean random = config.getBoolean(base + ".random", false);

            result.add(new Scheduler(validTimes, timeZone, random, koths));
        }

        return result;
    }
}