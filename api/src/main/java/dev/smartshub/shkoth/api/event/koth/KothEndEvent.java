package dev.smartshub.shkoth.api.event.koth;

import dev.smartshub.shkoth.api.koth.Koth;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class KothEndEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Koth koth;
    private final Set<UUID> winners;
    private final EndReason reason;

    public enum EndReason {
        //TODO: force stop command
        TIME_EXPIRED,
        MANUAL_STOP,
        CAPTURE_COMPLETED
    }

    public KothEndEvent(Koth koth, EndReason reason) {
        this.koth = koth;
        this.winners = new HashSet<>(koth.getWinners());
        this.reason = reason;
    }

    public Koth getKoth() { return koth; }
    public Set<UUID> getWinners() { return new HashSet<>(winners); }
    public EndReason getReason() { return reason; }
    public List<Player> getWinnerPlayers() {
        return winners.stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public @NotNull HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
