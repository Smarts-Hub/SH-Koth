package dev.smartshub.shkoth.api.event.koth;

import dev.smartshub.shkoth.api.model.koth.Koth;
import dev.smartshub.shkoth.api.model.koth.guideline.KothState;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class KothStateChangeEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Koth koth;
    private final KothState oldState;
    private final KothState newState;
    private boolean cancelled = false;

    public KothStateChangeEvent(Koth koth, KothState oldState, KothState newState) {
        this.koth = koth;
        this.oldState = oldState;
        this.newState = newState;
    }

    public Koth getKoth() { return koth; }
    public KothState getOldState() { return oldState; }
    public KothState getNewState() { return newState; }

    @Override
    public boolean isCancelled() { return cancelled; }

    @Override
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

    @Override
    public @NotNull HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
