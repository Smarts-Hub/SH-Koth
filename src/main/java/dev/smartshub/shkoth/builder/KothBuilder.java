package dev.smartshub.shkoth.builder;

import dev.smartshub.shkoth.koth.core.Koth;
import dev.smartshub.shkoth.model.koth.command.Commands;
import dev.smartshub.shkoth.model.koth.guideline.KothType;
import dev.smartshub.shkoth.model.koth.guideline.Mode;
import dev.smartshub.shkoth.koth.core.variant.SoloKoth;
import dev.smartshub.shkoth.koth.core.variant.TeamKoth;
import dev.smartshub.shkoth.model.location.Area;
import dev.smartshub.shkoth.model.location.Corner;
import dev.smartshub.shkoth.model.reward.PhysicalReward;
import dev.smartshub.shkoth.model.time.Schedule;
import dev.smartshub.shkoth.storage.file.Configuration;
import dev.smartshub.shkoth.storage.file.FileManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KothBuilder {

    private final SchedulesBuilder schedulesBuilder = new SchedulesBuilder();
    private final PhysicalRewardsBuilder physicalRewardsBuilder = new PhysicalRewardsBuilder();

    public Set<Koth> loadKothsFromConfig() {

        Set<Koth> koths = new HashSet<>();

        for (Configuration config : FileManager.getAllFromFolder("koths")) {

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


            Koth koth;
            if (mode.type() == KothType.SOLO) {
                koth = new SoloKoth(id, displayName, maxDuration, captureTime,area, mode, schedules, commands, physicalRewards);
            } else {
                koth = new TeamKoth(id, displayName, maxDuration, captureTime,area, mode, schedules, commands, physicalRewards);
            }

            koths.add(koth);
        }

        return koths;
    }
}
