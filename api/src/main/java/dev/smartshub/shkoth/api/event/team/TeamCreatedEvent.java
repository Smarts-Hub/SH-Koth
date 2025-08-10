package dev.smartshub.shkoth.api.event.team;

import dev.smartshub.shkoth.api.team.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TeamCreatedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Team team;
    private final CreationReason reason;

    public enum CreationReason {
        MANUAL,
        AUTO_SOLO,
        PLUGIN_IMPORT
    }

    public TeamCreatedEvent(Team team, CreationReason reason) {
        this.team = team;
        this.reason = reason;
    }

    public Team getTeam() { return team; }
    public CreationReason getReason() { return reason; }
    public Player getLeaderPlayer() { return team.getLeaderPlayer(); }
    public boolean isSoloTeam() { return team.size() == 1; }

    @Override
    public @NotNull HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
