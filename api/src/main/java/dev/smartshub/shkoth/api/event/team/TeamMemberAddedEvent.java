package dev.smartshub.shkoth.api.event.team;

import dev.smartshub.shkoth.api.model.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TeamMemberAddedEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Team oldTeam;
    private final Team newTeam;
    private final UUID addedMember;
    private final UUID inviter;
    private boolean cancelled = false;

    public TeamMemberAddedEvent(Team oldTeam, Team newTeam, UUID addedMember, UUID inviter) {
        this.oldTeam = oldTeam;
        this.newTeam = newTeam;
        this.addedMember = addedMember;
        this.inviter = inviter;
    }

    public Team getOldTeam() { return oldTeam; }
    public Team getNewTeam() { return newTeam; }
    public UUID getAddedMember() { return addedMember; }
    public UUID getInviter() { return inviter; }

    public Player getAddedPlayer() {
        return addedMember != null ? Bukkit.getPlayer(addedMember) : null;
    }

    public Player getInviterPlayer() {
        return inviter != null ? Bukkit.getPlayer(inviter) : null;
    }

    public boolean wasInvited() { return inviter != null; }
    public int getNewTeamSize() { return newTeam.size(); }
    public int getOldTeamSize() { return oldTeam.size(); }

    @Override
    public boolean isCancelled() { return cancelled; }

    @Override
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

    @Override
    public @NotNull HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
