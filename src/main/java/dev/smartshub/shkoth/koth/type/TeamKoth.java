package dev.smartshub.shkoth.koth.type;

import dev.smartshub.shkoth.koth.AbstractKoth;
import dev.smartshub.shkoth.api.model.koth.command.Commands;
import dev.smartshub.shkoth.api.model.koth.guideline.KothState;
import dev.smartshub.shkoth.api.model.koth.guideline.Mode;
import dev.smartshub.shkoth.api.model.location.Area;
import dev.smartshub.shkoth.api.model.reward.PhysicalReward;
import dev.smartshub.shkoth.api.model.time.Schedule;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TeamKoth extends AbstractKoth {

    public TeamKoth(String id, String displayName, int duration, int captureTime, Area area, Mode mode, List<Schedule> schedules, Commands commands, List<PhysicalReward> physicalRewards) {
        super(id, displayName, duration, captureTime, area, mode, schedules, commands, physicalRewards);
    }

    @Override
    public @NotNull Set<UUID> getWinners() {
        return Set.of();
    }

    @Override
    public @NotNull Set<UUID> getPlayersInside() {
        return Set.of();
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
    public boolean canPlayerCapture(@NotNull Player player) {
        return false;
    }

    @Override
    public @Nullable Player getCurrentCapturerPlayer() {
        return null;
    }

    @Override
    public int getCaptureProgress() {
        return 0;
    }

    @Override
    public @NotNull List<Player> getPlayersInsideList() {
        return List.of();
    }

    @Override
    public @NotNull List<Player> getWinnerPlayers() {
        return List.of();
    }

    @Override
    protected void fireStateChangeEvent(KothState oldState, KothState newState) {

    }
}
