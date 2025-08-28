package dev.smartshub.shkoth.koth.tally.capture;

import dev.smartshub.shkoth.api.event.koth.PlayerStopKothCaptureEvent;
import dev.smartshub.shkoth.api.koth.Koth;
import dev.smartshub.shkoth.api.koth.tally.Tally;
import dev.smartshub.shkoth.api.team.TeamWrapper;
import dev.smartshub.shkoth.team.ContextualTeamTracker;
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
        ContextualTeamTracker tracker = (ContextualTeamTracker) koth.getTeamTracker();

        Set<TeamWrapper> eligibleTeams = koth.getPlayersInside().stream()
                .map(uuid -> {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null || !koth.canPlayerCapture(player)) return null;

                    return tracker.getTeamForKoth(uuid, koth.isSolo());
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (eligibleTeams.isEmpty()) {
            koth.stopCapture(PlayerStopKothCaptureEvent.StopReason.PLAYER_LEFT_ZONE);
            return;
        }

        if (koth.getCurrentCapturingTeam() == null) {
            TeamWrapper firstTeam = eligibleTeams.iterator().next();
            koth.startCapture(firstTeam);
            return;
        }

        TeamWrapper currentTeamWrapper = new TeamWrapper(
                koth.getCurrentCapturingTeam().getLeader(),
                koth.getCurrentCapturingTeam().getMembers(),
                koth.getCurrentCapturingTeam().getDisplayName(),
                koth.isSolo()
        );

        if (eligibleTeams.contains(currentTeamWrapper)) {
            koth.checkCaptureProgress(koth.getCurrentCapturingTeam());
        } else {
            TeamWrapper newTeam = eligibleTeams.iterator().next();
            koth.stopCapture(PlayerStopKothCaptureEvent.StopReason.PLAYER_LEFT_ZONE);
            koth.startCapture(newTeam);
        }
    }
}
