package dev.smartshub.shkoth.koth.type;

import dev.smartshub.shkoth.api.model.team.TeamTracker;
import dev.smartshub.shkoth.koth.AbstractKoth;
import dev.smartshub.shkoth.api.model.koth.command.Commands;
import dev.smartshub.shkoth.api.model.koth.guideline.KothState;
import dev.smartshub.shkoth.api.model.location.Area;
import dev.smartshub.shkoth.api.model.reward.PhysicalReward;
import dev.smartshub.shkoth.api.model.time.Schedule;
import dev.smartshub.shkoth.koth.KothTeamTracker;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TeamKoth extends AbstractKoth {

    private final TeamTracker teamTracker;

    private final boolean denyEnterWithoutTeam;
    private final boolean createTeamIfNotExistsOnEnter;

    public TeamKoth(String id, String displayName, int duration, int captureTime, boolean denyEnterWithoutTeam, boolean createTeamIfNotExistsOnEnter,
                    Area area, int teamSize, List<Schedule> schedules, Commands commands, List<PhysicalReward> physicalRewards) {
        super(id, displayName, duration, captureTime, area, schedules, commands, physicalRewards);
        teamTracker = new KothTeamTracker(teamSize);
        this.denyEnterWithoutTeam = denyEnterWithoutTeam;
        this.createTeamIfNotExistsOnEnter = createTeamIfNotExistsOnEnter;
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
