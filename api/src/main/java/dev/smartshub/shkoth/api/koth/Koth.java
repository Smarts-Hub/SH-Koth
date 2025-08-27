package dev.smartshub.shkoth.api.koth;

import dev.smartshub.shkoth.api.event.koth.KothEndEvent;
import dev.smartshub.shkoth.api.event.koth.PlayerStopKothCaptureEvent;
import dev.smartshub.shkoth.api.koth.command.Commands;
import dev.smartshub.shkoth.api.koth.guideline.KothState;
import dev.smartshub.shkoth.api.location.Area;
import dev.smartshub.shkoth.api.reward.PhysicalReward;
import dev.smartshub.shkoth.api.team.KothTeam;
import dev.smartshub.shkoth.api.team.track.TeamTracker;
import dev.smartshub.shkoth.api.schedule.Schedule;
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
    void stop(KothEndEvent.EndReason reason);
    void stopCapture(PlayerStopKothCaptureEvent.StopReason reason);
    void startCapture(KothTeam team);
    void checkCaptureProgress(KothTeam team);
    void tick();
    boolean isRunning();
    boolean isSolo();
    boolean isTeam();
    void playerEnter(Player player);
    void playerLeave(Player player);
    void removePlayerDirectly(UUID playerUUID);
    
    boolean isInsideArea(@NotNull Player player);
    boolean canPlayerCapture(@NotNull Player player);
    
    @Nullable Player getCurrentCapturerPlayer();
    int getCaptureProgress();
    @NotNull TeamTracker getTeamTracker();

    KothTeam getCurrentCapturingTeam();
}