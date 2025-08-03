package dev.smartshub.shkoth.koth.type;


import dev.smartshub.shkoth.api.event.KothStateChangeEvent;
import dev.smartshub.shkoth.koth.AbstractKoth;
import dev.smartshub.shkoth.api.model.koth.command.Commands;
import dev.smartshub.shkoth.api.model.koth.guideline.KothState;
import dev.smartshub.shkoth.api.model.koth.guideline.Mode;
import dev.smartshub.shkoth.api.model.location.Area;
import dev.smartshub.shkoth.api.model.reward.PhysicalReward;
import dev.smartshub.shkoth.api.model.time.Schedule;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SoloKoth extends AbstractKoth {

    public SoloKoth(String id, String displayName, int duration, int captureTime, Area area, Mode mode, List<Schedule> schedules, Commands commands, List<PhysicalReward> physicalRewards) {
        super(id, displayName, duration, captureTime, area, mode, schedules, commands, physicalRewards);
    }

    @Override
    public void start() {
        setState(KothState.RUNNING);
        this.remainingTime = duration;
        this.inside.clear();
        this.winners.clear();
        this.currentCapturer = null;
        this.captureStartTime = 0;

        // TODO: send message and run start commands
    }

    @Override
    public void stop() {
        setState(KothState.INACTIVE);
        this.inside.clear();
        this.currentCapturer = null;
        this.captureStartTime = 0;

        //TODO: send message and run end commands and rewards

    }

    @Override
    public void tick() {
        if (!isRunning()) return;

        // Decrease time and check if it has ended
        remainingTime--;
        if (remainingTime <= 0) {
            stop();
            return;
        }

        handleCapture();
        // Should send messages eventually??!
    }

    private void handleCapture() {
        List<UUID> eligibleCapturers = inside.stream()
                .filter(uuid -> {
                    Player player = Bukkit.getPlayer(uuid);
                    return player != null && canPlayerCapture(player);
                })
                .toList();

        // Nobody inside -> no capture
        if (eligibleCapturers.isEmpty()) {
            if (currentCapturer != null) {
                stopCapture();
            }
            return;
        }

        // No current capturer -> get the first eligible player
        if (currentCapturer == null) {
            UUID firstPlayer = eligibleCapturers.getFirst();
            Player player = Bukkit.getPlayer(firstPlayer);
            startCapture(player);
            return;
        }

        // Verifications about capturer
        if (eligibleCapturers.contains(currentCapturer)) {
            Player currentPlayer = Bukkit.getPlayer(currentCapturer);
            if (currentPlayer == null) return;
            checkCaptureProgress(currentPlayer);
        }
        UUID newCapturer = eligibleCapturers.getFirst();
        Player newPlayer = Bukkit.getPlayer(newCapturer);
        stopCapture();
        startCapture(newPlayer);
    }

    private void startCapture(Player player) {
        setCurrentCapturer(player);
        //TODO: send message/broadcast
    }

    private void stopCapture() {
        Player previousCapturer = Bukkit.getPlayer(currentCapturer);
        if (previousCapturer != null) {
            long elapsedTime = (System.currentTimeMillis() - captureStartTime) / 1000;
        }
        this.currentCapturer = null;
        this.captureStartTime = 0;
        //TODO: send message/broadcast
    }

    private void checkCaptureProgress(Player player) {
        long elapsedTime = (System.currentTimeMillis() - captureStartTime) / 1000;

        if (elapsedTime >= captureTime) {
            completeCapture(player);
            return;
        }
        // TODO: instead 5 secs, make it configurable
        if (elapsedTime % 5 == 0 && elapsedTime > 0) {
            long remaining = captureTime - elapsedTime;
            //TODO: message
        }
    }

    private void completeCapture(Player player) {
        winners.add(player.getUniqueId());
        //TODO: send message/broadcast about capture completion
        stop();
    }

    @Override
    public void onPlayerEnter(Player player) {
        //TODO
    }

    @Override
    public void onPlayerLeave(Player player) {
        //TODO
    }

    @Override
    public boolean canPlayerCapture(@NotNull Player player) {
        return player != null
                && !player.isDead()
                && player.getGameMode() == GameMode.SURVIVAL
                && !winners.contains(player.getUniqueId()); // prevents bugs
    }


    private void giveRewards() {
        //TODO: give physical rewards to the winner and commands
    }

    @Override
    protected void fireStateChangeEvent(KothState oldState, KothState newState) {
        KothStateChangeEvent event = new KothStateChangeEvent(this, oldState, newState);
        Bukkit.getPluginManager().callEvent(event);
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
}
