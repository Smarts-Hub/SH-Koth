package dev.smartshub.shkoth.builder;

import dev.smartshub.shkoth.api.builder.Builder;
import dev.smartshub.shkoth.api.config.ConfigContainer;
import dev.smartshub.shkoth.api.koth.Koth;
import dev.smartshub.shkoth.api.koth.command.Commands;
import dev.smartshub.shkoth.api.koth.guideline.KothType;
import dev.smartshub.shkoth.api.location.Area;
import dev.smartshub.shkoth.api.location.Corner;
import dev.smartshub.shkoth.api.reward.PhysicalReward;
import dev.smartshub.shkoth.api.location.schedule.Schedule;
import dev.smartshub.shkoth.api.team.track.TeamTracker;
import dev.smartshub.shkoth.builder.mapper.PhysicalRewardsMapper;
import dev.smartshub.shkoth.builder.mapper.SchedulesMapper;

import java.util.List;


public class KothBuilder implements Builder<Koth, ConfigContainer> {

    private final TeamTracker teamTracker;
    private final SchedulesMapper schedulesMapper = new SchedulesMapper();
    private final PhysicalRewardsMapper physicalRewardsMapper = new PhysicalRewardsMapper();

    public KothBuilder(TeamTracker teamTracker) {
        this.teamTracker = teamTracker;
    }

    @Override
    public Koth build(ConfigContainer config) {

        String id = config.getName().replace(".yml", "");
        String displayName = config.getString("display-name", id);
        int maxDuration = config.getInt("max-duration", 600);
        int captureTime = config.getInt("capture-time", 30);

        Area area = new Area(
                config.getString("world", "world"),
                new Corner(
                        config.getInt("corner-1.x", -16),
                        config.getInt("corner-1.y", 64),
                        config.getInt("corner-1.z", -16)
                ),
                new Corner(
                        config.getInt("corner-2.x", 16),
                        config.getInt("corner-2.y", 80),
                        config.getInt("corner-2.z", 16)
                )
        );

        KothType kothType = KothType.fromString(config.getString("type", "capture"));

        final boolean isSolo = config.getBoolean("solo-mode", true);

        // Load schedules and rewards code is "dirty", doing it in a separate class to maintain clean code
        List<Schedule> schedules = schedulesMapper.map(config);
        List<PhysicalReward> physicalRewards = physicalRewardsMapper.map(config);


        Commands commands = new Commands(
                config.getStringList("commands-perform.start", List.of()),
                config.getStringList("commands-perform.end", List.of()),
                config.getStringList("commands-perform.to-winners", List.of())
        );


        boolean denyEnterWithoutTeam = config.getBoolean("deny-entry-if-not-in-team", false);
        boolean createTeamIfNotExistsOnEnter = config.getBoolean("create-team-if-not-exists-on-enter", true);


        return new dev.smartshub.shkoth.koth.Koth(teamTracker, id, displayName, maxDuration, captureTime,area, schedules, commands,
                physicalRewards, isSolo, denyEnterWithoutTeam, createTeamIfNotExistsOnEnter, kothType);
    }
}
