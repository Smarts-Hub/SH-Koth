package dev.smartshub.shkoth.api.event.team;

import dev.smartshub.shkoth.api.team.KothTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TeamCreatedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final KothTeam team;
    private final CreationReason reason;

    public enum CreationReason {
        MANUAL,
        AUTO_SOLO,
        PLUGIN_IMPORT
    }

    public TeamCreatedEvent(KothTeam team, CreationReason reason) {
        this.team = team;
        this.reason = reason;
    }

    public KothTeam getTeam() { return team; }
    public CreationReason getReason() { return reason; }
    public Player getLeaderPlayer() { return team.getLeaderPlayer(); }
    public boolean isSoloTeam() { return team.getMembers().size() == 1; }

    @Override
    public @NotNull HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
