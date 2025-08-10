package dev.smartshub.shkoth.api.event.koth;

import dev.smartshub.shkoth.api.koth.Koth;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class KothStartEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Koth koth;
    private boolean cancelled = false;

    public KothStartEvent(Koth koth) {
        this.koth = koth;
    }

    public Koth getKoth() { return koth; }

    @Override
    public boolean isCancelled() { return cancelled; }

    @Override
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

    @Override
    public @NotNull HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
