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
import dev.smartshub.shkoth.api.team.TeamTracker;
import dev.smartshub.shkoth.api.schedule.Schedule;
import dev.smartshub.shkoth.api.koth.tally.TallyFactory;
import dev.smartshub.shkoth.team.tracker.GlobalTeamTracker;
import dev.smartshub.shkoth.service.koth.KothRewardService;
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
    private final GlobalTeamTracker teamTracker;
    private final Tally tally;
    private final boolean isSolo;
    private final boolean denyEnterWithoutTeam;
    private final boolean createTeamIfNotExistsOnEnter;

    private Team currentCapturingTeam;
    private long captureStartTime;

    public Koth(String id, String displayName, int duration, int captureTime, Area area,
                List<Schedule> schedules, Commands commands, List<PhysicalReward> physicalRewards,
                boolean isSolo, boolean denyEnterWithoutTeam, boolean createTeamIfNotExistsOnEnter, KothType type) {
        super(id, displayName, duration, captureTime, area, schedules, commands, physicalRewards);

        this.teamTracker = GlobalTeamTracker.getInstance();
        this.tally = TallyFactory.create(type, this);
        this.isSolo = isSolo;
        this.denyEnterWithoutTeam = denyEnterWithoutTeam;
        this.createTeamIfNotExistsOnEnter = createTeamIfNotExistsOnEnter;
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
        if (!canTeamCapture(team)) return;

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

        handlePlayerEntry(player);
    }

    @Override
    public void playerLeave(Player player) {
        UUID playerId = player.getUniqueId();
        boolean wasInside = inside.remove(playerId);

        if (wasInside) {
            boolean wasCapturing = currentCapturingTeam != null && currentCapturingTeam.contains(playerId);
            eventDispatcher.firePlayerLeaveKothDuringRunEvent(this, player, wasCapturing);

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
        }
    }

    @Override
    public boolean canPlayerCapture(@NotNull Player player) {
        return !player.isDead() && player.getGameMode() == GameMode.SURVIVAL && !winners.contains(player.getUniqueId());
    }

    public boolean canTeamCapture(Team team) {
        if (team == null) return false;

        if (isSolo && team.members().size() > 1) {
            return false;
        }

        return team.getOnlineMembers().stream()
                .anyMatch(this::canPlayerCapture);
    }

    private void handlePlayerEntry(Player player) {
        UUID playerId = player.getUniqueId();
        Team playerTeam = teamTracker.getTeamFrom(playerId);

        if (denyEnterWithoutTeam && playerTeam == null) {
            if (createTeamIfNotExistsOnEnter) {
                playerTeam = teamTracker.createTeam(playerId);
            } else {
                player.sendMessage("§cNecesitas estar en un equipo para entrar a este KOTH!");
                return;
            }
        }

        if (playerTeam != null && !canTeamCapture(playerTeam)) {
            if (isSolo) {
                player.sendMessage("§cEste es un KOTH solo y tu equipo tiene más de 1 miembro!");
            } else {
                player.sendMessage("§cTu equipo no puede participar en este KOTH!");
            }
            return;
        }

        inside.add(playerId);
    }

    private void giveRewards() {
        rewardService.grantRewards();
    }

    @Override
    public boolean isSolo() {
        return isSolo;
    }

    @Override
    public boolean isTeam() {
        return !isSolo;
    }

    public boolean isDenyEnterWithoutTeam() {
        return denyEnterWithoutTeam;
    }

    public boolean isCreateTeamIfNotExistsOnEnter() {
        return createTeamIfNotExistsOnEnter;
    }

    public @NotNull TeamTracker getTeamTracker() {
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