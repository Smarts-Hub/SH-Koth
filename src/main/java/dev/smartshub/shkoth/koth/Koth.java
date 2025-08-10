package dev.smartshub.shkoth.koth;

import dev.smartshub.shkoth.api.event.koth.*;
import dev.smartshub.shkoth.api.koth.AbstractKoth;
import dev.smartshub.shkoth.api.koth.KothEventDispatcher;
import dev.smartshub.shkoth.api.koth.command.Commands;
import dev.smartshub.shkoth.api.koth.guideline.KothState;
import dev.smartshub.shkoth.api.koth.guideline.KothType;
import dev.smartshub.shkoth.api.koth.tally.Tally;
import dev.smartshub.shkoth.api.location.Area;
import dev.smartshub.shkoth.api.reward.PhysicalReward;
import dev.smartshub.shkoth.api.team.Team;
import dev.smartshub.shkoth.api.schedule.Schedule;
import dev.smartshub.shkoth.api.koth.tally.TallyFactory;
import dev.smartshub.shkoth.koth.reward.PhysicalRewardAdder;
import dev.smartshub.shkoth.koth.track.KothTeamTracker;
import dev.smartshub.shkoth.service.config.ConfigService;
import dev.smartshub.shkoth.service.koth.KothRewardService;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Koth extends AbstractKoth {

    private final KothEventDispatcher eventDispatcher = new KothEventDispatcher();
    private final KothRewardService rewardService = new KothRewardService(this);
    private final PhysicalRewardAdder physicalRewardAdder = new PhysicalRewardAdder();
    private final KothTeamTracker teamTracker;
    private final Tally tally;
    private Team currentCapturingTeam;
    private long captureStartTime;

    public Koth(String id, String displayName, int duration, int captureTime, Area area,
                List<Schedule> schedules, Commands commands, List<PhysicalReward> physicalRewards,
                int maxTeamSize, boolean denyEnterWithoutTeam, boolean createTeamIfNotExistsOnEnter, KothType type) {
        super(id, displayName, duration, captureTime, area, schedules, commands, physicalRewards);

        this.teamTracker = new KothTeamTracker(maxTeamSize);
        this.tally = TallyFactory.create(type, this);
    }

    @Override
    public void start() {
        KothStartEvent event = eventDispatcher.fireKothStartEvent(this);
        if(event.isCancelled()) return;

        setState(KothState.RUNNING);
        this.remainingTime = duration;
        this.inside.clear();
        this.winners.clear();
        this.currentCapturingTeam = null;
        this.captureStartTime = 0;
    }

    @Override
    public void stop(KothEndEvent.EndReason reason) {
        eventDispatcher.fireKothEndEvent(this, reason);
        setState(KothState.INACTIVE);
        this.inside.clear();
        this.currentCapturingTeam = null;
        this.captureStartTime = 0;
        this.teamTracker.clearAllTeams();
    }

    @Override
    public void tick() {
        if (!isRunning()) return;

        teamTracker.cleanupInvalidTeams();

        remainingTime--;
        if (remainingTime <= 0) {
            stop(KothEndEvent.EndReason.TIME_EXPIRED);
            return;
        }

        handleCapture();
    }

    private void handleCapture() {
        tally.handle();
    }

    public void startCapture(Team team) {
        Player representativePlayer = team.getLeaderPlayer();
        if (representativePlayer == null) return;

        PlayerStartKothCaptureEvent event = eventDispatcher.firePlayerStartKothCaptureEvent(this, representativePlayer, currentCapturingTeam != null ? currentCapturingTeam.leader() : null);
        if(event.isCancelled()) return;

        this.currentCapturingTeam = team;
        this.captureStartTime = System.currentTimeMillis();
    }

    public void stopCapture(PlayerStopKothCaptureEvent.StopReason reason) {
        if (currentCapturingTeam == null) return;

        Player previousCapturer = Bukkit.getPlayer(currentCapturingTeam.leader());
        long elapsedTime = (System.currentTimeMillis() - captureStartTime) / 1000;

        eventDispatcher.firePlayerStopKothCaptureEvent(this, previousCapturer, elapsedTime, reason);

        this.currentCapturingTeam = null;
        this.captureStartTime = 0;
    }

    public void checkCaptureProgress(Team team) {
        long elapsedTime = (System.currentTimeMillis() - captureStartTime) / 1000;

        if (elapsedTime >= captureTime) {
            completeCapture(team);
            return;
        }
        // TODO: instead 5 secs, make it configurable
        if (elapsedTime % 5 == 0 && elapsedTime > 0) {
            long remaining = captureTime - elapsedTime;
            //TODO: message
        }
    }

    private void completeCapture(Team team) {
        winners.addAll(team.members());
        stop(KothEndEvent.EndReason.CAPTURE_COMPLETED);
        giveRewards();
    }

    @Override
    public void playerEnter(Player player) {
        PlayerEnterKothDuringRunEvent event = eventDispatcher.firePlayerEnterKothDuringRunEvent(this, player);
        if (!event.isCancelled()) return;
        //TODO: make cancellable with the "border" of the Koth
    }

    @Override
    public void playerLeave(Player player) {
        UUID playerId = player.getUniqueId();
        boolean wasInside = inside.remove(playerId);

        if (wasInside) {
            boolean wasCapturing = currentCapturingTeam != null && currentCapturingTeam.contains(playerId);
            eventDispatcher.firePlayerLeaveKothDuringRunEvent(this, player, wasCapturing);

            //TODO: make cancellable with the "border" of the Koth

            if (wasCapturing) {
                stopCapture(PlayerStopKothCaptureEvent.StopReason.PLAYER_LEFT_ZONE);
            }

        }
    }

    public void removePlayerDirectly(UUID playerId) {
        boolean wasInside = inside.remove(playerId);

        if (wasInside) {
            if (currentCapturingTeam != null && currentCapturingTeam.contains(playerId)) {
                stopCapture(PlayerStopKothCaptureEvent.StopReason.PLAYER_DISCONNECTED);
            }

            teamTracker.removeMember(playerId);
        }

    }

    public void addPhysicalReward(ConfigService configService, ItemStack item, int amount) {
        physicalRewardAdder.addRewards(configService, this, item, amount);
    }

    @Override
    public boolean canPlayerCapture(@NotNull Player player) {
        return !player.isDead() && player.getGameMode() == GameMode.SURVIVAL && !winners.contains(player.getUniqueId());
    }

    private void giveRewards() {
        rewardService.grantRewards();
    }

    public @NotNull KothTeamTracker getTeamTracker() {
        return teamTracker;
    }

    public Team getCurrentCapturingTeam() {
        return currentCapturingTeam;
    }

    @Override
    public @NotNull Set<UUID> getWinners() {
        return winners;
    }

    @Override
    public @NotNull Set<UUID> getPlayersInside() {
        return inside;
    }

    @Override
    public @Nullable Player getCurrentCapturerPlayer() {
        return currentCapturingTeam != null ? currentCapturingTeam.getLeaderPlayer() : null;
    }

    @Override
    public int getCaptureProgress() {
        if (currentCapturingTeam == null || captureStartTime == 0) return 0;
        long elapsedTime = (System.currentTimeMillis() - captureStartTime) / 1000;
        return (int) Math.min(100, (elapsedTime * 100) / captureTime);
    }

}