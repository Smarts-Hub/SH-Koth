package dev.smartshub.shkoth.builder;

import dev.smartshub.shkoth.model.koth.Koth;
import dev.smartshub.shkoth.model.koth.command.Commands;
import dev.smartshub.shkoth.model.koth.guideline.KothType;
import dev.smartshub.shkoth.model.koth.guideline.Mode;
import dev.smartshub.shkoth.model.koth.location.Area;
import dev.smartshub.shkoth.model.koth.location.Corner;
import dev.smartshub.shkoth.model.koth.reward.PhysicalReward;
import dev.smartshub.shkoth.model.koth.time.Schedule;
import dev.smartshub.shkoth.storage.file.Configuration;
import dev.smartshub.shkoth.storage.file.FileManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class KothBuilder {

    public Set<Koth> loadKoths() {

        Set<Koth> koths = Set.of();

        for(Configuration config : FileManager.getAllFromFolder("koths")){

            String id = config.getName();
            String displayName = config.getString("display-name");
            int maxDuration = config.getInt("max-duration");
            int captureTime = config.getInt("capture-time");

            Area area = new Area(
                    config.getString("world"),
                    new Corner(
                            config.getInt("corner1.x"),
                            config.getInt("corner1.y"),
                            config.getInt("corner1.z")
                    ),
                    new Corner(
                            config.getInt("corner1.x"),
                            config.getInt("corner1.y"),
                            config.getInt("corner1.z")
                    )
            );

            Mode mode = new Mode(
                    KothType.valueOf(config.getString("type")),
                    config.getInt("team-size")
            );

            List<Schedule> schedules = new ArrayList<>();
            // for(){} TODO: Implement schedule loading

            Commands commands = new Commands(
                    config.getStringList("start-commands"),
                    config.getStringList("end-commands"),
                    config.getStringList("winners-commands")
            );

            List<PhysicalReward> physicalRewards = new ArrayList<>();
            // for(){} TODO: Implement physical rewards loading

            Koth koth; // TODO Implement Koth subclasses based on type
        }


        return koths;
    }

}
