package dev.smartshub.shkoth.api.koth;

import dev.smartshub.shkoth.api.event.koth.KothEndEvent;
import dev.smartshub.shkoth.api.event.koth.KothStateChangeEvent;
import dev.smartshub.shkoth.api.event.koth.PlayerStartKothCaptureEvent;
import dev.smartshub.shkoth.api.koth.command.Commands;
import dev.smartshub.shkoth.api.koth.guideline.KothState;
import dev.smartshub.shkoth.api.location.Area;
import dev.smartshub.shkoth.api.reward.PhysicalReward;
import dev.smartshub.shkoth.api.team.Team;
import dev.smartshub.shkoth.api.schedule.Schedule;
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
        protected final List<Schedule> schedules;
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
                            List<Schedule> schedules, Commands commands,
                            List<PhysicalReward> physicalRewards) {
                this.id = id;
                this.displayName = displayName;
                this.duration = duration;
                this.captureTime = captureTime;
                this.area = area;
                this.schedules = List.copyOf(schedules);
                this.commands = commands;
                this.physicalRewards = List.copyOf(physicalRewards);
                this.remainingTime = duration;

                this.eventDispatcher = new KothEventDispatcher();
        }

        public @NotNull String getId() { return id; }
        public @NotNull String getDisplayName() { return displayName; }
        public int getDuration() { return duration; }
        public int getCaptureTime() { return captureTime; }
        public @NotNull Area getArea() { return area; }
        public @NotNull List<Schedule> getSchedules() { return schedules; }
        public @NotNull Commands getCommands() { return commands; }
        public @NotNull List<PhysicalReward> getPhysicalRewards() { return physicalRewards; }

        public @NotNull KothState getState() { return state; }
        public UUID getCurrentCapturer() { return currentCapturer; }
        public long getCaptureStartTime() { return captureStartTime; }
        public int getRemainingTime() { return remainingTime; }

        // Abstract methods to be implemented by subclasses
        public abstract void start();
        public abstract void stop(KothEndEvent.EndReason reason);
        public abstract void tick();
        public abstract void playerEnter(Player player);
        public abstract void playerLeave(Player player);
        public abstract boolean canPlayerCapture(@NotNull Player player);
        public abstract Team getCurrentCapturingTeam();

        // Common utility methods
        public boolean isRunning() {
                return state == KothState.RUNNING;
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

}
