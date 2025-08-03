package dev.smartshub.shkoth.api.event.koth;


import dev.smartshub.shkoth.api.model.koth.Koth;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerStopKothCaptureEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Koth koth;
    private final Player player;
    private final long captureTimeElapsed;
    private final StopReason reason;

    public enum StopReason {
        PLAYER_LEFT_ZONE,
        PLAYER_DIED,
        PLAYER_DISCONNECTED,
        KOTH_ENDED,
        CAPTURE_COMPLETED,
        ANOTHER_PLAYER_TOOK_OVER
    }

    public PlayerStopKothCaptureEvent(Koth koth, Player player, long captureTimeElapsed, StopReason reason) {
        this.koth = koth;
        this.player = player;
        this.captureTimeElapsed = captureTimeElapsed;
        this.reason = reason;
    }

    public Koth getKoth() { return koth; }
    public Player getPlayer() { return player; }
    public long getCaptureTimeElapsed() { return captureTimeElapsed; }
    public long getCaptureTimeElapsedSeconds() { return captureTimeElapsed / 1000; }
    public StopReason getReason() { return reason; }

    @Override
    public @NotNull HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}