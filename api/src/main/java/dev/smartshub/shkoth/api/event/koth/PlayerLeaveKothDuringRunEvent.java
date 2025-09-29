package dev.smartshub.shkoth.api.event.koth;


import dev.smartshub.shkoth.api.event.key.DiscordKey;
import dev.smartshub.shkoth.api.koth.Koth;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerLeaveKothDuringRunEvent extends Event implements DiscordKey {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Koth koth;
    private final Player player;
    private final boolean wasCapturing;

    public PlayerLeaveKothDuringRunEvent(Koth koth, Player player, boolean wasCapturing) {
        this.koth = koth;
        this.player = player;
        this.wasCapturing = wasCapturing;
    }

    public Koth getKoth() { return koth; }
    public Player getPlayer() { return player; }
    public boolean wasCapturing() { return wasCapturing; }

    @Override
    public @NotNull HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }

    @Override
    public String getDiscordKey() {
        return "player-leave";
    }
}
