package dev.smartshub.shkoth.api.event.koth;

import dev.smartshub.shkoth.api.koth.Koth;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerStartKothCaptureEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Koth koth;
    private final Player player;
    private final UUID previousCapturer;
    private boolean cancelled = false;

    public PlayerStartKothCaptureEvent(Koth koth, Player player, UUID previousCapturer) {
        this.koth = koth;
        this.player = player;
        this.previousCapturer = previousCapturer;
    }

    public Koth getKoth() { return koth; }
    public Player getPlayer() { return player; }
    public UUID getPreviousCapturer() { return previousCapturer; }
    public boolean hadPreviousCapturer() { return previousCapturer != null; }

    @Override
    public boolean isCancelled() { return cancelled; }

    @Override
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

    @Override
    public @NotNull HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
