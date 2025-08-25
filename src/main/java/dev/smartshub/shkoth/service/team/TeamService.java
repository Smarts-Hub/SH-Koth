package dev.smartshub.shkoth.service.team;

import dev.smartshub.shkoth.api.team.Team;
import dev.smartshub.shkoth.team.tracker.GlobalTeamTracker;
import org.bukkit.entity.Player;

import java.util.*;

public class TeamService {

    private final GlobalTeamTracker tracker = GlobalTeamTracker.getInstance();
    private final Map<UUID, UUID> pendingInvites = new HashMap<>(); // target -> leader

    public Team createTeam(Player leader) {
        return tracker.createTeam(leader.getUniqueId());
    }

    public void disbandTeam(Player leader) {
        tracker.dissolveTeam(leader.getUniqueId());
    }

    public void leaveTeam(Player player) {
        tracker.removeMember(player.getUniqueId());
    }

    public void kickMember(Player leader, Player target) {
        Team team = tracker.getTeamFrom(leader.getUniqueId());
        if (team == null || !team.isLeader(leader.getUniqueId())) {
            throw new IllegalStateException("You are not a team leader.");
        }
        tracker.removeMember(target.getUniqueId());
    }

    public void setLeader(Player currentLeader, Player newLeader) {
        Team team = tracker.getTeamFrom(currentLeader.getUniqueId());
        if (team == null || !team.isLeader(currentLeader.getUniqueId())) {
            throw new IllegalStateException("You are not a team leader.");
        }
        tracker.updateLeader(currentLeader.getUniqueId(), newLeader.getUniqueId());
    }

    public void invite(Player leader, Player target) {
        Team team = tracker.getTeamFrom(leader.getUniqueId());
        if (team == null || !team.isLeader(leader.getUniqueId())) {
            throw new IllegalStateException("You are not a team leader.");
        }
        pendingInvites.put(target.getUniqueId(), leader.getUniqueId());
    }

    public boolean acceptInvite(Player player) {
        UUID leaderId = pendingInvites.remove(player.getUniqueId());
        if (leaderId == null) return false;

        Team team = tracker.getTeamByLeader(leaderId).orElse(null);
        if (team == null) return false;

        tracker.addMember(player.getUniqueId(), team);
        return true;
    }

    public boolean declineInvite(Player player) {
        return pendingInvites.remove(player.getUniqueId()) != null;
    }

    public Team getTeam(Player player) {
        return tracker.getTeamFrom(player.getUniqueId());
    }

    public Collection<Team> listTeams() {
        return tracker.getAllTeams();
    }
}
