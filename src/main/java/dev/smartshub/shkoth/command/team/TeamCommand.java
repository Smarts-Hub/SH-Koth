package dev.smartshub.shkoth.command.team;

import dev.smartshub.shkoth.service.team.TeamInformationService;
import dev.smartshub.shkoth.service.team.TeamInvitationService;
import dev.smartshub.shkoth.service.team.TeamUpdatingService;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

@Command("team")
public class TeamCommand {

    private final TeamUpdatingService teamUpdatingService;
    private final TeamInformationService teamInformationService;
    private final TeamInvitationService teamInvitationService;

    public TeamCommand(TeamUpdatingService teamUpdatingService,
                       TeamInformationService teamInformationService,
                       TeamInvitationService teamInvitationService) {
        this.teamUpdatingService = teamUpdatingService;
        this.teamInformationService = teamInformationService;
        this.teamInvitationService = teamInvitationService;
    }

    @Subcommand("create")
    public void create(BukkitCommandActor actor) {
        if(!actor.isPlayer()) return;
        teamUpdatingService.createTeam(actor.asPlayer());
    }

    @Subcommand("leave")
    public void leave(BukkitCommandActor actor) {
        if(!actor.isPlayer()) return;
        teamUpdatingService.leaveTeam(actor.asPlayer());
    }

    @Subcommand("disband")
    public void disband(BukkitCommandActor actor) {
        if(!actor.isPlayer()) return;
        teamUpdatingService.disbandTeam(actor.asPlayer());
    }

    @Subcommand("invite <player>")
    public void invite(BukkitCommandActor actor, String player) {
        if(!actor.isPlayer()) return;
        teamInvitationService.invite(actor.asPlayer(), player);
    }

    @Subcommand("kick <player>")
    public void kick(BukkitCommandActor actor, String player) {
        if(!actor.isPlayer()) return;
        teamUpdatingService.kickMember(actor.asPlayer(), player);
    }

    @Subcommand("set-leader <player>")
    public void setLeader(BukkitCommandActor actor, String player) {
        if(!actor.isPlayer()) return;
        teamUpdatingService.setLeader(actor.asPlayer(), player);
    }

    @Subcommand("invite accept")
    public void acceptInvite(BukkitCommandActor actor) {
        if(!actor.isPlayer()) return;
        teamInvitationService.acceptInvite(actor.asPlayer());
    }

    @Subcommand("invite decline")
    public void declineInvite(BukkitCommandActor actor) {
        if(!actor.isPlayer()) return;
        teamInvitationService.declineInvite(actor.asPlayer());
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