package dev.smartshub.shkoth.api.event.koth;


import dev.smartshub.shkoth.api.event.key.DiscordKey;
import dev.smartshub.shkoth.api.koth.Koth;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerEnterKothDuringRunEvent extends Event implements Cancellable, DiscordKey {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Koth koth;
    private final Player player;
    private boolean cancelled = false;

    public PlayerEnterKothDuringRunEvent(Koth koth, Player player) {
        this.koth = koth;
        this.player = player;
    }

    public Koth getKoth() { return koth; }
    public Player getPlayer() { return player; }

    @Override
    public boolean isCancelled() { return cancelled; }

    @Override
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

    @Override
    public @NotNull HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }

    @Override
    public String getDiscordKey() {
        return "player-enter";
    }
}