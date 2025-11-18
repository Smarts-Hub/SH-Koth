package dev.smartshub.shkoth.api.koth;

import dev.smartshub.shkoth.api.event.dispatcher.KothEventDispatcher;
import dev.smartshub.shkoth.api.event.koth.KothEndEvent;
import dev.smartshub.shkoth.api.event.koth.KothStateChangeEvent;
import dev.smartshub.shkoth.api.event.koth.PlayerStartKothCaptureEvent;
import dev.smartshub.shkoth.api.koth.command.Commands;
import dev.smartshub.shkoth.api.koth.guideline.KothState;
import dev.smartshub.shkoth.api.location.Area;
import dev.smartshub.shkoth.api.reward.PhysicalReward;
import dev.smartshub.shkoth.api.team.KothTeam;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractKoth implements Koth {
        // Immutable data (configuration)
        protected final String id;
        protected final String displayName;
        protected final int duration;
        protected final int captureTime;
        protected final Area area;
        protected final Commands commands;
        protected final List<PhysicalReward> physicalRewards;

        private final KothEventDispatcher eventDispatcher;

        // Mutable data (runtime)
        protected KothState state = KothState.INACTIVE;
        protected Set<UUID> inside;
        protected Set<UUID> winners;
        protected UUID currentCapturer;
        protected long captureStartTime;
        protected int remainingTime;

        public AbstractKoth(String id, String displayName, int duration, int captureTime, Area area,
                            Commands commands, List<PhysicalReward> physicalRewards) {
                this.id = id;
                this.displayName = displayName;
                this.duration = duration;
                this.captureTime = captureTime;
                this.area = area;
                this.commands = commands;
                this.physicalRewards = new ArrayList<>(physicalRewards);
                this.remainingTime = duration;

                this.inside = ConcurrentHashMap.newKeySet();
                this.winners = ConcurrentHashMap.newKeySet();
                this.currentCapturer = null;
                this.captureStartTime = 0;

                this.eventDispatcher = new KothEventDispatcher();
        }

        public @NotNull String getId() { return id; }
        public @NotNull String getDisplayName() { return displayName; }
        public int getDuration() { return duration; }
        public int getCaptureTime() { return captureTime; }
        public @NotNull Area getArea() { return area; }
        public @NotNull Commands getCommands() { return commands; }
        public @NotNull List<PhysicalReward> getPhysicalRewards() { return physicalRewards; }

        public @NotNull KothState getState() { return state; }
        public UUID getCurrentCapturer() { return currentCapturer; }
        public long getCaptureStartTime() { return captureStartTime; }
        public long getSecondsUntilCaptureComplete() {
                if (captureStartTime == 0) return -1;
                long elapsedSeconds = (System.currentTimeMillis() - captureStartTime) / 1000;
                long remaining = captureTime - elapsedSeconds;
                return (int) Math.max(0, remaining);
        }

        public int getRemainingTime() { return remainingTime; }

        // Abstract methods to be implemented by subclasses
        public abstract void start();
        public abstract void stop(KothEndEvent.EndReason reason);
        public abstract void tick();
        public abstract void playerEnter(Player player);
        public abstract void playerLeave(Player player);
        public abstract boolean canPlayerCapture(@NotNull Player player);
        public abstract boolean isScoreboardEnabled();

        public abstract boolean isSolo();

        public abstract boolean isTeam();

        public abstract boolean isCapturing();

        public abstract KothTeam getCurrentCapturingTeam();

        // Common utility methods
        public boolean isRunning() {
                return state == KothState.RUNNING || state == KothState.CAPTURING;
        }

        public boolean isInsideArea(@NotNull Player player) {
                return area.contains(player.getLocation());
        }

        protected void setState(KothState newState) {
                KothStateChangeEvent event = eventDispatcher.fireKothStateChangeEvent(this, this.state, newState);
                if(event.isCancelled()) return;

                this.state = newState;
        }

        protected void setCurrentCapturer(Player player) {
                PlayerStartKothCaptureEvent event = eventDispatcher.firePlayerStartKothCaptureEvent(this, player, currentCapturer);
                if(event.isCancelled()) return;

                this.currentCapturer = player.getUniqueId();
                this.captureStartTime = System.currentTimeMillis();
        }

        @Override
        public @NotNull Set<UUID> getPlayersInside() {
                return inside;
        }

        @Override
        public boolean isPlayerEligibleToStay(@NotNull Player player) {
                // Default logic: A player is eligible to stay if they are eligible to capture.
                // Specific KOTH types can override this for more complex rules.
                return canPlayerCapture(player);
        }

        @Override
        public void removePlayerDirectly(UUID playerId) {
                if (inside.remove(playerId)) {
                        // Crucial check: if the removed player was the capturer, reset the capture state
                        // to prevent a "ghost" capture from a logged-out player.
                        if (Objects.equals(playerId, currentCapturer)) {
                                resetCaptureState();
                        }
                }
        }

        /**
         * Helper method to cleanly reset the KOTH's capturing state.
         */
        protected void resetCaptureState() {
                this.currentCapturer = null;
                this.captureStartTime = 0;
                if (this.state == KothState.CAPTURING) {
                        setState(KothState.RUNNING);
                }
        }
}
