package dev.smartshub.shkoth.koth.tally.capture;

import dev.smartshub.shkoth.api.event.koth.PlayerStopKothCaptureEvent;
import dev.smartshub.shkoth.api.koth.Koth;
import dev.smartshub.shkoth.api.koth.tally.Tally;
import dev.smartshub.shkoth.api.team.KothTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CaptureTally implements Tally {

    private final Koth koth;

    public CaptureTally(Koth koth) {
        this.koth = koth;
    }

    @Override
    public void handle(){
        Set<KothTeam> eligibleTeams = koth.getPlayersInside().stream()
                .map(uuid -> {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null || !koth.canPlayerCapture(player)) return null;

                    KothTeam team = koth.getTeamTracker().getTeamFrom(uuid);
                    if (team == null) {
                        team = koth.getTeamTracker().createTeam(uuid);
                    }
                    return team;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (eligibleTeams.isEmpty()) {
            koth.stopCapture(PlayerStopKothCaptureEvent.StopReason.PLAYER_LEFT_ZONE);
            return;
        }

        if (koth.getCurrentCapturingTeam() == null) {
            KothTeam firstTeam = eligibleTeams.iterator().next();
            koth.startCapture(firstTeam);
            return;
        }

        if (eligibleTeams.contains(koth.getCurrentCapturingTeam())) {
            koth.checkCaptureProgress(koth.getCurrentCapturingTeam());
        } else {
            KothTeam newTeam = eligibleTeams.iterator().next();
            koth.stopCapture(PlayerStopKothCaptureEvent.StopReason.PLAYER_LEFT_ZONE);
            koth.startCapture(newTeam);
        }
    }

}
