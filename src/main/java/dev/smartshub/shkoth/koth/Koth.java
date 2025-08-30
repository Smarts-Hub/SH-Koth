package dev.smartshub.shkoth.koth;

import dev.smartshub.shkoth.api.event.koth.*;
import dev.smartshub.shkoth.api.koth.AbstractKoth;
import dev.smartshub.shkoth.api.event.dispatcher.KothEventDispatcher;
import dev.smartshub.shkoth.api.koth.command.Commands;
import dev.smartshub.shkoth.api.koth.guideline.KothState;
import dev.smartshub.shkoth.api.koth.guideline.KothType;
import dev.smartshub.shkoth.api.koth.tally.Tally;
import dev.smartshub.shkoth.api.location.Area;
import dev.smartshub.shkoth.api.reward.PhysicalReward;
import dev.smartshub.shkoth.api.team.KothTeam;
import dev.smartshub.shkoth.api.team.TeamWrapper;
import dev.smartshub.shkoth.api.team.track.TeamTracker;
import dev.smartshub.shkoth.api.location.schedule.Schedule;
import dev.smartshub.shkoth.api.koth.tally.TallyFactory;
import dev.smartshub.shkoth.service.koth.KothRewardService;
import dev.smartshub.shkoth.team.ContextualTeamTracker;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Koth extends AbstractKoth {

    private final KothEventDispatcher eventDispatcher = new KothEventDispatcher();
    private final KothRewardService rewardService = new KothRewardService(this);
    private final TeamTracker teamTracker;
    private final Tally tally;
    private final boolean isSolo;
    private final boolean denyEnterWithoutTeam;
    private final boolean createTeamIfNotExistsOnEnter;

    private KothTeam currentCapturingTeam;
    private long captureStartTime;

    public Koth(TeamTracker teamTracker, String id, String displayName, int duration, int captureTime, Area area,
                List<Schedule> schedules, Commands commands, List<PhysicalReward> physicalRewards,
                boolean isSolo, boolean denyEnterWithoutTeam, boolean createTeamIfNotExistsOnEnter, KothType type) {
        super(id, displayName, duration, captureTime, area, schedules, commands, physicalRewards);

        this.teamTracker = teamTracker;
        this.tally = TallyFactory.create(type, this);
        this.isSolo = isSolo;
        this.denyEnterWithoutTeam = denyEnterWithoutTeam;
        this.createTeamIfNotExistsOnEnter = createTeamIfNotExistsOnEnter;
    }

    @Override
    public void start() {
        KothStartEvent event = eventDispatcher.fireKothStartEvent(this);
        if (event.isCancelled()) return;

        eventDispatcher.fireKothStateChangeEvent(this, getState(), KothState.RUNNING);
        setState(KothState.RUNNING);
        this.remainingTime = duration;
        this.inside.clear();
        this.winners.clear();
        resetCapture();
    }

    @Override
    public void stop(KothEndEvent.EndReason reason) {
        eventDispatcher.fireKothEndEvent(this, reason);
        eventDispatcher.fireKothStateChangeEvent(this, getState(), KothState.INACTIVE);
        setState(KothState.INACTIVE);
        this.inside.clear();
        resetCapture();
    }

    @Override
    public void tick() {
        if (getState() == KothState.INACTIVE) return;

        remainingTime--;
        if (remainingTime <= 0) {
            stop(KothEndEvent.EndReason.TIME_EXPIRED);
            return;
        }

        tally.handle();
    }

    public void startCapture(KothTeam team) {
        if (!canTeamCapture(team)) return;

        Player representativePlayer = team.getLeaderPlayer();
        if (representativePlayer == null) return;

        PlayerStartKothCaptureEvent event = eventDispatcher.firePlayerStartKothCaptureEvent(
                this, representativePlayer, currentCapturingTeam != null ? currentCapturingTeam.getLeader() : null);
        if (event.isCancelled()) return;

        eventDispatcher.fireKothStateChangeEvent(this, getState(), KothState.CAPTURING);
        setState(KothState.CAPTURING);

        this.currentCapturingTeam = team;
        this.captureStartTime = System.currentTimeMillis();
    }

    public void stopCapture(PlayerStopKothCaptureEvent.StopReason reason) {
        if (currentCapturingTeam == null) return;

        Player previousCapturer = Bukkit.getPlayer(currentCapturingTeam.getLeader());
        long elapsedTime = (System.currentTimeMillis() - captureStartTime) / 1000;

        eventDispatcher.firePlayerStopKothCaptureEvent(this, previousCapturer, elapsedTime, reason);
        eventDispatcher.fireKothStateChangeEvent(this, getState(), KothState.RUNNING);
        setState(KothState.RUNNING);

        resetCapture();
    }

    public void checkCaptureProgress(KothTeam team) {
        if (currentCapturingTeam == null || captureStartTime == 0) return;

        long elapsedTime = (System.currentTimeMillis() - captureStartTime) / 1000;
        if (elapsedTime >= captureTime) {
            completeCapture(team);
        }
    }

    private void completeCapture(KothTeam team) {
        winners.addAll(team.getMembers());
        stop(KothEndEvent.EndReason.CAPTURE_COMPLETED);
        rewardService.grantRewards();
    }

    private void resetCapture() {
        this.currentCapturingTeam = null;
        this.captureStartTime = 0;
    }

    @Override
    public void playerEnter(Player player) {
        PlayerEnterKothDuringRunEvent event = eventDispatcher.firePlayerEnterKothDuringRunEvent(this, player);
        if (event.isCancelled()) return;

        if (!isPlayerEligibleToEnter(player)) return;

        inside.add(player.getUniqueId());
    }

    @Override
    public void playerLeave(Player player) {
        UUID playerId = player.getUniqueId();
        if (!inside.remove(playerId)) return;

        boolean wasCapturing = currentCapturingTeam != null && currentCapturingTeam.contains(playerId);
        eventDispatcher.firePlayerLeaveKothDuringRunEvent(this, player, wasCapturing);

        if (wasCapturing) {
            stopCapture(PlayerStopKothCaptureEvent.StopReason.PLAYER_LEFT_ZONE);
        }
    }

    public void removePlayerDirectly(UUID playerId) {
        if (!inside.remove(playerId)) return;

        if (currentCapturingTeam != null && currentCapturingTeam.contains(playerId)) {
            stopCapture(PlayerStopKothCaptureEvent.StopReason.PLAYER_DISCONNECTED);
        }
    }

    public boolean isPlayerEligibleToStay(Player player) {
        if (!canPlayerCapture(player)) return false;
        if (teamTracker == null) return isSolo;

        if (isSolo) return true;

        TeamWrapper playerTeam = getPlayerTeam(player.getUniqueId());
        return !(denyEnterWithoutTeam && playerTeam == null) && (playerTeam == null || canTeamCapture(playerTeam));
    }

    @Override
    public boolean canPlayerCapture(@NotNull Player player) {
        return !player.isDead() &&
                player.getGameMode() == GameMode.SURVIVAL &&
                !winners.contains(player.getUniqueId());
    }

    public boolean canTeamCapture(KothTeam team) {
        if (team == null) return false;
        if (isSolo && team.getMembers().size() > 1) return false;

        return team.getOnlineMembers().stream().anyMatch(this::canPlayerCapture);
    }

    private boolean isPlayerEligibleToEnter(Player player) {
        if (!canPlayerCapture(player)) return false;
        if (teamTracker == null) return isSolo;

        TeamWrapper playerTeam = getPlayerTeam(player.getUniqueId());

        if (!isSolo && denyEnterWithoutTeam && playerTeam == null) {
            if (createTeamIfNotExistsOnEnter) {
                ContextualTeamTracker tracker = (ContextualTeamTracker) teamTracker;
                playerTeam = tracker.createInternalTeam(player.getUniqueId(), Integer.MAX_VALUE);
                if (playerTeam == null) return false;
            } else {
                return false;
            }
        }

        return playerTeam == null || canTeamCapture(playerTeam);
    }

    private TeamWrapper getPlayerTeam(UUID playerId) {
        ContextualTeamTracker tracker = (ContextualTeamTracker) teamTracker;
        return tracker.getTeamForKoth(playerId, isSolo);
    }

    @Override
    public boolean isSolo() { return isSolo; }

    @Override
    public boolean isTeam() { return !isSolo; }

    @Override
    public boolean isCapturing() { return currentCapturingTeam != null; }

    public boolean isDenyEnterWithoutTeam() { return denyEnterWithoutTeam; }

    public boolean isCreateTeamIfNotExistsOnEnter() { return createTeamIfNotExistsOnEnter; }

    public @NotNull TeamTracker getTeamTracker() { return teamTracker; }

    public KothTeam getCurrentCapturingTeam() { return currentCapturingTeam; }

    @Override
    public @NotNull Set<UUID> getWinners() { return winners; }

    @Override
    public @NotNull Set<UUID> getPlayersInside() { return inside; }

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