package dev.smartshub.shkoth.command.team;

import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

@Command("team")
public class TeamCommand {

    @Subcommand("create")
    public void create(BukkitCommandActor actor) {
        if(!actor.isPlayer()) return;

    }

    @Subcommand("leave")
    public void leave(BukkitCommandActor actor) {
        if(!actor.isPlayer()) return;

    }

    @Subcommand("disband")
    public void disband(BukkitCommandActor actor) {
        if(!actor.isPlayer()) return;

    }

    @Subcommand("invite <player>")
    public void invite(BukkitCommandActor actor, String player) {
        if(!actor.isPlayer()) return;

    }

    @Subcommand("kick <player>")
    public void kick(BukkitCommandActor actor, String player) {
        if(!actor.isPlayer()) return;

    }

    @Subcommand("set-leader <player>")
    public void setLeader(BukkitCommandActor actor, String player) {
        if(!actor.isPlayer()) return;

    }

    @Subcommand("invite accept")
    public void acceptInvite(BukkitCommandActor actor) {
        if(!actor.isPlayer()) return;

    }

    @Subcommand("invite decline")
    public void declineInvite(BukkitCommandActor actor) {
        if(!actor.isPlayer()) return;

    }

    @Subcommand("chat <message>")
    public void chat(BukkitCommandActor actor, String message) {
        if(!actor.isPlayer()) return;

    }

    @Subcommand("info")
    public void info(BukkitCommandActor actor) {
        if(!actor.isPlayer()) return;

    }

}