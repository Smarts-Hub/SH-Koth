package dev.smartshub.shkoth.builder.mapper;

import dev.smartshub.shkoth.api.model.builder.mapper.Mapper;
import dev.smartshub.shkoth.api.model.config.ConfigContainer;
import dev.smartshub.shkoth.api.model.time.Day;
import dev.smartshub.shkoth.api.model.time.Schedule;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SchedulesMapper implements Mapper<List<Schedule>, ConfigContainer> {

    @Override
    public List<Schedule> map(ConfigContainer config){
        List<Schedule> schedules = new ArrayList<>();

        ConfigurationSection section = config.getConfigurationSection("schedule");

        if (section == null) {
           Bukkit.getLogger().warning("No schedule section found in " + config.getName());
           return schedules;
        }

        for (String key : section.getKeys(false)) {
            ConfigurationSection entry = section.getConfigurationSection(key);
            if (entry == null) continue;

            String day = entry.getString("day", "MONDAY");
            String hour = entry.getString("hour", "19:00");
            schedules.add(new Schedule(Day.valueOf(day), LocalTime.parse(hour)));
        }

        return schedules;
    }

}
