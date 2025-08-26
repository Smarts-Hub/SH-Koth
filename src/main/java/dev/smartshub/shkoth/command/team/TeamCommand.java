package dev.smartshub.shkoth.command.team;

import dev.smartshub.shkoth.service.team.TeamService;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

@Command("team")
public class TeamCommand {

    private final TeamService teamService;

    public TeamCommand(TeamService teamService) {
        this.teamService = teamService;
    }

    @Subcommand("create")
    public void create(BukkitCommandActor actor) {
        if(!actor.isPlayer()) return;
        teamService.createTeam(actor.asPlayer());
    }

    @Subcommand("leave")
    public void leave(BukkitCommandActor actor) {
        if(!actor.isPlayer()) return;
        teamService.leaveTeam(actor.asPlayer());
    }

    @Subcommand("disband")
    public void disband(BukkitCommandActor actor) {
        if(!actor.isPlayer()) return;
        teamService.disbandTeam(actor.asPlayer());
    }

    @Subcommand("invite <player>")
    public void invite(BukkitCommandActor actor, String player) {
        if(!actor.isPlayer()) return;
        teamService.invite(actor.asPlayer(), player);
    }

    @Subcommand("kick <player>")
    public void kick(BukkitCommandActor actor, String player) {
        if(!actor.isPlayer()) return;
        teamService.kickMember(actor.asPlayer(), player);
    }

    @Subcommand("set-leader <player>")
    public void setLeader(BukkitCommandActor actor, String player) {
        if(!actor.isPlayer()) return;
        teamService.setLeader(actor.asPlayer(), player);
    }

    @Subcommand("invite accept")
    public void acceptInvite(BukkitCommandActor actor) {
        if(!actor.isPlayer()) return;
        teamService.acceptInvite(actor.asPlayer());
    }

    @Subcommand("invite decline")
    public void declineInvite(BukkitCommandActor actor) {
        if(!actor.isPlayer()) return;
        teamService.declineInvite(actor.asPlayer());
    }

    @Subcommand("chat <message>")
    public void chat(BukkitCommandActor actor, String message) {
        if(!actor.isPlayer()) return;
        teamService.sendTeamMessage(actor.asPlayer(), message);
    }

    @Subcommand("info")
    public void info(BukkitCommandActor actor) {
        if(!actor.isPlayer()) return;
        teamService.sendTeamInfo(actor.asPlayer());
    }

}