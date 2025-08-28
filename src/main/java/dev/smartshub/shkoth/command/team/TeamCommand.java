package dev.smartshub.shkoth.command.team;

import dev.smartshub.shkoth.service.team.TeamHandlingService;
import dev.smartshub.shkoth.service.team.TeamInformationService;
import dev.smartshub.shkoth.service.team.TeamInviteService;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

@Command("team")
public class TeamCommand {

    private final TeamHandlingService teamHandlingService;
    private final TeamInviteService teamInviteService;
    private final TeamInformationService teamInformationService;

    public TeamCommand(TeamHandlingService teamHandlingService, TeamInviteService teamInviteService, TeamInformationService teamInformationService) {
        this.teamHandlingService = teamHandlingService;
        this.teamInviteService = teamInviteService;
        this.teamInformationService = teamInformationService;
    }

    @Subcommand("create")
    public void create(BukkitCommandActor actor) {
        if(!actor.isPlayer()) return;
        teamHandlingService.createTeam(actor.asPlayer(), Integer.MAX_VALUE);
    }

    @Subcommand("leave")
    public void leave(BukkitCommandActor actor) {
        if(!actor.isPlayer()) return;
        teamHandlingService.leaveTeam(actor.asPlayer());
    }

    @Subcommand("disband")
    public void disband(BukkitCommandActor actor) {
        if(!actor.isPlayer()) return;
        teamHandlingService.disbandTeam(actor.asPlayer());
    }

    @Subcommand("invite <player>")
    public void invite(BukkitCommandActor actor, String player) {
        if(!actor.isPlayer()) return;
        teamInviteService.sendInvitation(actor.asPlayer(), player);
    }

    @Subcommand("kick <player>")
    public void kick(BukkitCommandActor actor, String player) {
        if(!actor.isPlayer()) return;
        teamHandlingService.kickMember(actor.asPlayer(), player);
    }

    @Subcommand("set-leader <player>")
    public void setLeader(BukkitCommandActor actor, String player) {
        if(!actor.isPlayer()) return;
        teamHandlingService.transferLeadership(actor.asPlayer(), player);
    }

    @Subcommand("invite accept")
    public void acceptInvite(BukkitCommandActor actor) {
        if(!actor.isPlayer()) return;
        teamInviteService.acceptInvitation(actor.asPlayer());
    }

    @Subcommand("invite decline")
    public void declineInvite(BukkitCommandActor actor) {
        if(!actor.isPlayer()) return;
        teamInviteService.declineInvitation(actor.asPlayer());
    }

    @Subcommand("chat <message>")
    public void chat(BukkitCommandActor actor, String message) {
        if(!actor.isPlayer()) return;
        teamInformationService.sendTeamMessage(actor.asPlayer(), message);
    }

    @Subcommand("info")
    public void info(BukkitCommandActor actor) {
        if(!actor.isPlayer()) return;
        teamInformationService.sendTeamInfo(actor.asPlayer());
    }

}