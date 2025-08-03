package dev.smartshub.shkoth.builder;

import dev.smartshub.shkoth.api.model.koth.command.Commands;
import dev.smartshub.shkoth.api.model.koth.guideline.KothType;
import dev.smartshub.shkoth.api.model.koth.guideline.Mode;
import dev.smartshub.shkoth.api.model.location.Area;
import dev.smartshub.shkoth.api.model.location.Corner;
import dev.smartshub.shkoth.api.model.reward.PhysicalReward;
import dev.smartshub.shkoth.api.model.time.Schedule;
import dev.smartshub.shkoth.koth.AbstractKoth;
import dev.smartshub.shkoth.koth.type.SoloKoth;
import dev.smartshub.shkoth.koth.type.TeamKoth;
import dev.smartshub.shkoth.storage.file.Configuration;

import java.util.List;


public class KothBuilder {

    private final SchedulesBuilder schedulesBuilder = new SchedulesBuilder();
    private final PhysicalRewardsBuilder physicalRewardsBuilder = new PhysicalRewardsBuilder();

    public AbstractKoth buildKothFromFile(Configuration config) {

        String id = config.getName().replace(".yml", "");
        String displayName = config.getString("display-name");
        int maxDuration = config.getInt("max-duration");
        int captureTime = config.getInt("capture-time");

        Area area = new Area(
                config.getString("world"),
                new Corner(
                        config.getInt("corner-1.x"),
                        config.getInt("corner-1.y"),
                        config.getInt("corner-1.z")
                ),
                new Corner(
                        config.getInt("corner-2.x"),
                        config.getInt("corner-2.y"),
                        config.getInt("corner-2.z")
                )
        );

        Mode mode = new Mode(
                KothType.valueOf(config.getString("type")),
                config.getInt("team-size")
        );

        // Load schedules and rewards code is "dirty", doing it in a separate class to maintain clean code
        List<Schedule> schedules = schedulesBuilder.getSchedulesFrom(config);
        List<PhysicalReward> physicalRewards = physicalRewardsBuilder.getPhysicalRewardsFrom(config);


        Commands commands = new Commands(
                config.getStringList("commands-perform.start"),
                config.getStringList("commands-perform.end"),
                config.getStringList("commands-perform.to-winners")
        );

        AbstractKoth abstractKoth;
        if (mode.type() == KothType.SOLO) {
            abstractKoth = new SoloKoth(id, displayName, maxDuration, captureTime,area, mode, schedules, commands, physicalRewards);
        } else {
            abstractKoth = new TeamKoth(id, displayName, maxDuration, captureTime,area, mode, schedules, commands, physicalRewards);
        }

        return abstractKoth;
    }
}
