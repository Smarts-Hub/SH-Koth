package dev.smartshub.shkoth.api.event.team;


import dev.smartshub.shkoth.api.team.KothTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class MemberKickedFromTeamEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final KothTeam oldTeam;
    private final UUID kickedMember;
    private final UUID kicker;

    public MemberKickedFromTeamEvent(KothTeam oldTeam, UUID kickedMember, UUID kicker) {
        this.oldTeam = oldTeam;
        this.kickedMember = kickedMember;
        this.kicker = kicker;
    }

    public KothTeam getOldTeam() { return oldTeam; }
    public UUID getKickedMember() { return kickedMember; }
    public UUID getKicker() { return kicker; }

    public Player getKickedPlayer() {
        return kickedMember != null ? Bukkit.getPlayer(kickedMember) : null;
    }

    public Player getKickerPlayer() {
        return kicker != null ? Bukkit.getPlayer(kicker) : null;
    }


    @Override
    public @NotNull HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }

}
