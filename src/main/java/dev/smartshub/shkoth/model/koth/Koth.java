package dev.smartshub.shkoth.model.koth;

import dev.smartshub.shkoth.model.koth.command.Commands;
import dev.smartshub.shkoth.model.koth.guidelines.KothState;
import dev.smartshub.shkoth.model.koth.guidelines.Mode;
import dev.smartshub.shkoth.model.koth.location.Area;
import dev.smartshub.shkoth.model.koth.reward.PhysicalReward;
import dev.smartshub.shkoth.model.koth.time.Schedule;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public abstract class Koth {
        // Inmutable data (configuration)
        protected final String id;
        protected final String displayName;
        protected final int duration;
        protected final int captureTime;
        protected final Area area;
        protected final Mode mode;
        protected final List<Schedule> schedules;
        protected final List<Commands> commands;
        protected final List<PhysicalReward> physicalRewards;

        // Mutable data (runtime)
        protected KothState state = KothState.INACTIVE;
        protected Set<UUID> inside;
        protected Set<UUID> winners;
        protected UUID currentCapturer;
        protected long captureStartTime;
        protected int remainingTime;

        public Koth(String id, String displayName, int duration, int captureTime, Area area,
                    Mode mode, List<Schedule> schedules, List<Commands> commands,
                    List<PhysicalReward> physicalRewards) {
                this.id = id;
                this.displayName = displayName;
                this.duration = duration;
                this.captureTime = captureTime;
                this.area = area;
                this.mode = mode;
                this.schedules = List.copyOf(schedules);
                this.commands = List.copyOf(commands);
                this.physicalRewards = List.copyOf(physicalRewards);
                this.remainingTime = duration;
        }

        public String getId() { return id; }
        public String getDisplayName() { return displayName; }
        public int getDuration() { return duration; }
        public int getCaptureTime() { return captureTime; }
        public Area getArea() { return area; }
        public Mode getMode() { return mode; }
        public List<Schedule> getSchedules() { return schedules; }
        public List<Commands> getCommands() { return commands; }
        public List<PhysicalReward> getPhysicalRewards() { return physicalRewards; }

        public KothState getState() { return state; }
        public UUID getCurrentCapturer() { return currentCapturer; }
        public long getCaptureStartTime() { return captureStartTime; }
        public int getRemainingTime() { return remainingTime; }

        // Abstract methods to be implemented by subclasses
        public abstract void start();
        public abstract void stop();
        public abstract void tick();
        public abstract void onPlayerEnter(Player player);
        public abstract void onPlayerLeave(Player player);
        public abstract boolean canPlayerCapture(Player player);

        // Common utility methods
        public boolean isRunning() {
                return state == KothState.RUNNING;
        }

        public boolean isInsideArea(Player player) {
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
