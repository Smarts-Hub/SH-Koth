package dev.smartshub.shkoth.model.koth;

import dev.smartshub.shkoth.model.koth.command.Commands;
import dev.smartshub.shkoth.model.koth.guidelines.Mode;
import dev.smartshub.shkoth.model.koth.location.Area;
import dev.smartshub.shkoth.model.koth.reward.PhysicalReward;
import dev.smartshub.shkoth.model.koth.time.Schedule;

import java.util.List;

public record Koth(
        String id,
        String displayName,
        int duration,
        Area area,
        Mode mode,
        List<Schedule> schedules,
        List<Commands> commands,
        List<PhysicalReward> physicalRewards
        ) {
}
