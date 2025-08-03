package dev.smartshub.shkoth.koth;

import dev.smartshub.shkoth.api.core.Koth;
import dev.smartshub.shkoth.api.model.koth.command.Commands;
import dev.smartshub.shkoth.api.model.koth.guideline.KothState;
import dev.smartshub.shkoth.api.model.koth.guideline.Mode;
import dev.smartshub.shkoth.api.model.location.Area;
import dev.smartshub.shkoth.api.model.reward.PhysicalReward;
import dev.smartshub.shkoth.api.model.time.Schedule;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractKoth implements Koth {
        // Immutable data (configuration)
        protected final String id;
        protected final String displayName;
        protected final int duration;
        protected final int captureTime;
        protected final Area area;
        protected final Mode mode;
        protected final List<Schedule> schedules;
        protected final Commands commands;
        protected final List<PhysicalReward> physicalRewards;

        // Mutable data (runtime)
        protected KothState state = KothState.INACTIVE;
        protected Set<UUID> inside;
        protected Set<UUID> winners;
        protected UUID currentCapturer;
        protected long captureStartTime;
        protected int remainingTime;

        public AbstractKoth(String id, String displayName, int duration, int captureTime, Area area,
                            Mode mode, List<Schedule> schedules, Commands commands,
                            List<PhysicalReward> physicalRewards) {
                this.id = id;
                this.displayName = displayName;
                this.duration = duration;
                this.captureTime = captureTime;
                this.area = area;
                this.mode = mode;
                this.schedules = List.copyOf(schedules);
                this.commands = commands;
                this.physicalRewards = List.copyOf(physicalRewards);
                this.remainingTime = duration;
        }

        public @NotNull String getId() { return id; }
        public @NotNull String getDisplayName() { return displayName; }
        public int getDuration() { return duration; }
        public int getCaptureTime() { return captureTime; }
        public @NotNull Area getArea() { return area; }
        public @NotNull Mode getMode() { return mode; }
        public @NotNull List<Schedule> getSchedules() { return schedules; }
        public @NotNull Commands getCommands() { return commands; }
        public @NotNull List<PhysicalReward> getPhysicalRewards() { return physicalRewards; }

        public @NotNull KothState getState() { return state; }
        public UUID getCurrentCapturer() { return currentCapturer; }
        public long getCaptureStartTime() { return captureStartTime; }
        public int getRemainingTime() { return remainingTime; }

        // Abstract methods to be implemented by subclasses
        public abstract void start();
        public abstract void stop();
        public abstract void tick();
        public abstract void onPlayerEnter(Player player);
        public abstract void onPlayerLeave(Player player);
        public abstract boolean canPlayerCapture(@NotNull Player player);

        // Common utility methods
        public boolean isRunning() {
                return state == KothState.RUNNING;
        }

        public boolean isInsideArea(@NotNull Player player) {
                return area.contains(player.getLocation());
        }

        protected void setState(KothState newState) {
                KothState oldState = this.state;
                this.state = newState;
                fireStateChangeEvent(oldState, newState);
        }

        protected void setCurrentCapturer(Player player) {
                this.currentCapturer = player.getUniqueId();
                this.captureStartTime = System.currentTimeMillis();
        }

        protected abstract void fireStateChangeEvent(KothState oldState, KothState newState);
}
