package dev.smartshub.shkoth.builder;

import dev.smartshub.shkoth.model.time.Day;
import dev.smartshub.shkoth.model.time.Schedule;
import dev.smartshub.shkoth.storage.file.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SchedulesBuilder {

    public List<Schedule> getSchedulesFrom(Configuration config){
        List<Schedule> schedules = new ArrayList<>();

        ConfigurationSection section = config.getConfigurationSection("schedule");

        if (section == null) {
           Bukkit.getLogger().warning("No schedule section found in " + config.getName());
           return schedules;
        }

        for (String key : section.getKeys(false)) {
            ConfigurationSection entry = section.getConfigurationSection(key);
            if (entry == null) continue;

            String day = entry.getString("day");
            String hour = entry.getString("hour");
            schedules.add(new Schedule(Day.valueOf(day), LocalTime.parse(hour)));
        }

        return schedules;
    }

}
