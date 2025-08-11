package dev.smartshub.shkoth.command.team;

import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

@Command("team")
public class TeamCommand {

    @Subcommand("create")
    public void create() {

    }

    @Subcommand("join <player>")
    public void join(BukkitCommandActor actor, String player) {

    }

    @Subcommand("leave")
    public void leave() {

    }

    @Subcommand("disband")
    public void disband() {

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
