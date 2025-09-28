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
    public void handle() {
        Set<TeamWrapper> eligibleTeams = getEligibleTeams();

        if (eligibleTeams.isEmpty()) {
            if (koth.isCapturing()) {
                koth.stopCapture(PlayerStopKothCaptureEvent.StopReason.PLAYER_LEFT_ZONE);
            }
            return;
        }

        if (!koth.isCapturing()) {
            startNewCapture(eligibleTeams);
            return;
        }

        handleOngoingCapture(eligibleTeams);
    }

    private Set<TeamWrapper> getEligibleTeams() {
        ContextualTeamTracker tracker = (ContextualTeamTracker) koth.getTeamTracker();

        return koth.getPlayersInside().stream()
                .map(uuid -> {
                    Player player = Bukkit.getPlayer(uuid);
                    return (player != null && koth.canPlayerCapture(player))
                            ? tracker.getTeamForKoth(uuid, koth.isSolo())
                            : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private void startNewCapture(Set<TeamWrapper> eligibleTeams) {
        TeamWrapper teamToCapture = eligibleTeams.iterator().next();
        koth.startCapture(teamToCapture);
    }

    private void handleOngoingCapture(Set<TeamWrapper> eligibleTeams) {
        boolean currentTeamStillEligible = eligibleTeams.stream()
                .anyMatch(team -> koth.getCurrentCapturingTeam() != null &&
                        team.getLeader().equals(koth.getCurrentCapturingTeam().getLeader()));

        if (currentTeamStillEligible) {
            koth.checkCaptureProgress(koth.getCurrentCapturingTeam());
        } else {
            koth.stopCapture(PlayerStopKothCaptureEvent.StopReason.PLAYER_LEFT_ZONE);
            startNewCapture(eligibleTeams);
        }
    }
}