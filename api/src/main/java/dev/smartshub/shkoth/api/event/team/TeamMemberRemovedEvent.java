package dev.smartshub.shkoth.api.event.team;

import dev.smartshub.shkoth.api.team.KothTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TeamMemberRemovedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final KothTeam oldTeam;
    private final KothTeam newTeam;
    private final UUID removedMember;
    private final UUID remover;
    private final RemovalReason reason;

    public enum RemovalReason {
        LEFT_VOLUNTARILY,
        KICKED,
        DISCONNECTED,
        LEADER_CHANGE,
        TEAM_DISSOLVED
    }

    public TeamMemberRemovedEvent(KothTeam oldTeam, KothTeam newTeam, UUID removedMember,
                                  UUID remover, RemovalReason reason) {
        this.oldTeam = oldTeam;
        this.newTeam = newTeam;
        this.removedMember = removedMember;
        this.remover = remover;
        this.reason = reason;
    }

    public KothTeam getOldTeam() { return oldTeam; }
    public KothTeam getNewTeam() { return newTeam; }
    public UUID getRemovedMember() { return removedMember; }
    public UUID getRemover() { return remover; }
    public RemovalReason getReason() { return reason; }

    public Player getRemovedPlayer() {
        return removedMember != null ? Bukkit.getPlayer(removedMember) : null;
    }

    public Player getRemoverPlayer() {
        return remover != null ? Bukkit.getPlayer(remover) : null;
    }

    public boolean wasKicked() { return reason == RemovalReason.KICKED; }
    public boolean wasVoluntary() { return reason == RemovalReason.LEFT_VOLUNTARILY; }
    public boolean causedDissolution() { return newTeam == null; }
    public boolean wasLeader() { return oldTeam.isLeader(removedMember); }

    public int getOldTeamSize() { return oldTeam.getMembers().size(); }
    public int getNewTeamSize() { return newTeam != null ? newTeam.getMembers().size() : 0; }

    @Override
    public @NotNull HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
