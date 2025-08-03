package dev.smartshub.shkoth.api.event.team;

import dev.smartshub.shkoth.api.model.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TeamDissolvedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Team team;
    private final UUID dissolver;
    private final DissolutionReason reason;

    public enum DissolutionReason {
        LEADER_LEFT,
        MANUAL_DISSOLVE,
        EMPTY_TEAM,
        PLUGIN_SHUTDOWN,
    }

    public TeamDissolvedEvent(Team team, UUID dissolver, DissolutionReason reason) {
        this.team = team;
        this.dissolver = dissolver;
        this.reason = reason;
    }

    public Team getTeam() { return team; }
    public UUID getDissolver() { return dissolver; }
    public DissolutionReason getReason() { return reason; }

    public Player getDissolverPlayer() {
        return dissolver != null ? Bukkit.getPlayer(dissolver) : null;
    }

    public List<Player> getAffectedPlayers() {
        return team.getOnlineMembers();
    }

    public Set<UUID> getAffectedPlayerUUIDs() {
        return team.members();
    }

    @Override
    public @NotNull HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
