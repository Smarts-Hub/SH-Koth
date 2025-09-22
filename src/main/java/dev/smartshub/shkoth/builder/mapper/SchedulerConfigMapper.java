package dev.smartshub.shkoth.builder.mapper;

import dev.smartshub.shkoth.api.builder.mapper.Mapper;
import dev.smartshub.shkoth.api.config.ConfigContainer;
import dev.smartshub.shkoth.api.schedule.SchedulerConfig;

import java.util.*;

public class SchedulerConfigMapper implements Mapper<List<SchedulerConfig>, ConfigContainer> {

    @Override
    public List<SchedulerConfig> map(ConfigContainer config) {
        String tz = config.getString("time-zone", "server");
        Set<String> schedulerIds = config.getKeys("schedulers");
        List<SchedulerConfig> result = new ArrayList<>();

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

            result.add(SchedulerConfig.of(schedulerId, validTimes, tz, random, koths));
        }

        return result;
    }
}