package dev.smartshub.shkoth.model.koth.heritage;

import dev.smartshub.shkoth.model.koth.Koth;
import dev.smartshub.shkoth.model.koth.command.Commands;
import dev.smartshub.shkoth.model.koth.guideline.KothState;
import dev.smartshub.shkoth.model.koth.guideline.Mode;
import dev.smartshub.shkoth.model.koth.location.Area;
import dev.smartshub.shkoth.model.koth.reward.PhysicalReward;
import dev.smartshub.shkoth.model.koth.time.Schedule;
import org.bukkit.entity.Player;

import java.util.List;

public class SoloKoth extends Koth {

    public SoloKoth(String id, String displayName, int duration, int captureTime, Area area, Mode mode, List<Schedule> schedules, Commands commands, List<PhysicalReward> physicalRewards) {
        super(id, displayName, duration, captureTime, area, mode, schedules, commands, physicalRewards);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void tick() {

    }

    @Override
    public void onPlayerEnter(Player player) {

    }

    @Override
    public void onPlayerLeave(Player player) {

    }

    @Override
    public boolean canPlayerCapture(Player player) {
        return false;
    }

    @Override
    protected void fireStateChangeEvent(KothState oldState, KothState newState) {

    }
}
