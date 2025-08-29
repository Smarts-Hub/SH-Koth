package dev.smartshub.shkoth.command.handler.parameter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.exception.CommandErrorException;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

public class PlayerParameterType implements ParameterType<BukkitCommandActor, Player> {

    @Override
    public Player parse(MutableStringStream input, ExecutionContext<BukkitCommandActor> context) {
        String name = input.readString();
        Player player = Bukkit.getPlayerExact(name);
        if (player == null) {
            throw new CommandErrorException("No such player online: " + name);
        }
        return player;
    }

    @Override
    public SuggestionProvider<BukkitCommandActor> defaultSuggestions() {
        return context -> Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .toList();
    }
}
