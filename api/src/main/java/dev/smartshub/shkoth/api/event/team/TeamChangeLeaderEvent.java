package dev.smartshub.shkoth.api.event.team;

import dev.smartshub.shkoth.api.team.KothTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TeamChangeLeaderEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final KothTeam team;
    private final UUID oldLeader;
    private final UUID newLeader;
    private boolean cancelled = false;

    public TeamChangeLeaderEvent(KothTeam team, UUID oldLeader, UUID newLeader) {
        this.team = team;
        this.oldLeader = oldLeader;
        this.newLeader = newLeader;
    }

    public KothTeam getTeam() { return team; }
    public UUID getOldLeader() { return oldLeader; }
    public UUID getNewLeader() { return newLeader; }

    public Player getOldLeaderPlayer() {
        return oldLeader != null ? Bukkit.getPlayer(oldLeader) : null;
    }

    public Player getNewLeaderPlayer() {
        return newLeader != null ? Bukkit.getPlayer(newLeader) : null;
    }

    @Override
    public boolean isCancelled() { return cancelled; }

    @Override
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

    @Override
    public @NotNull HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}

