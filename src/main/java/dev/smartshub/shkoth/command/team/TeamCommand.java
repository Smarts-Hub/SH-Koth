package dev.smartshub.shkoth.command.team;

import dev.smartshub.shkoth.service.team.TeamChatService;
import dev.smartshub.shkoth.service.team.TeamService;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

@Command("team")
public class TeamCommand {

    private final TeamService teamService;
    private final TeamChatService teamChatService;

    public TeamCommand(TeamService teamService, TeamChatService teamChatService) {
        this.teamService = teamService;
        this.teamChatService = teamChatService;
    }

    @Subcommand("create")
    public void create(BukkitCommandActor actor) {

    }

    @Subcommand("join <player>")
    public void join(BukkitCommandActor actor, String player) {

    }

    @Subcommand("leave")
    public void leave(BukkitCommandActor actor) {

    }

    @Subcommand("disband")
    public void disband(BukkitCommandActor actor) {

    }

    @Subcommand("invite <player>")
    public void invite(BukkitCommandActor actor, String player) {

    }

    @Subcommand("kick <player>")
    public void kick(BukkitCommandActor actor, String player) {

    }

    @Subcommand("set-leader <player>")
    public void setLeader(BukkitCommandActor actor, String player) {

    }

    @Subcommand("invite accept")
    public void acceptInvite(BukkitCommandActor actor) {

    }

    @Subcommand("invite decline")
    public void declineInvite(BukkitCommandActor actor) {

    }

    @Subcommand("chat <message>")
    public void chat(BukkitCommandActor actor, String message) {

    }

    @Subcommand("info")
    public void info(BukkitCommandActor actor) {

    }

    @Subcommand("list")
    public void list(BukkitCommandActor actor) {

    }
}