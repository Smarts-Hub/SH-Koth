package dev.smartshub.shkoth.api.model.koth;

import dev.smartshub.shkoth.api.model.koth.command.Commands;
import dev.smartshub.shkoth.api.model.koth.guideline.KothState;
import dev.smartshub.shkoth.api.model.location.Area;
import dev.smartshub.shkoth.api.model.reward.PhysicalReward;
import dev.smartshub.shkoth.api.model.team.Team;
import dev.smartshub.shkoth.api.model.time.Schedule;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface Koth {
    
    @NotNull String getId();
    @NotNull String getDisplayName();

    int getDuration();
    int getCaptureTime();

    @NotNull Area getArea();
    @NotNull Team getTeam();
    @NotNull List<Schedule> getSchedules();
    @NotNull Commands getCommands();
    @NotNull List<PhysicalReward> getPhysicalRewards();
    
    @NotNull KothState getState();
    @Nullable UUID getCurrentCapturer();

    long getCaptureStartTime();
    int getRemainingTime();
    @NotNull Set<UUID> getWinners();
    @NotNull Set<UUID> getPlayersInside();
    
    void start();
    void stop();
    boolean isRunning();
    
    boolean isInsideArea(@NotNull Player player);
    boolean canPlayerCapture(@NotNull Player player);
    
    @Nullable Player getCurrentCapturerPlayer();
    int getCaptureProgress();
    @NotNull List<Player> getPlayersInsideList();
    @NotNull List<Player> getWinnerPlayers();
}