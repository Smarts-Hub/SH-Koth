package dev.smartshub.shkoth.command.handler.parameter;

import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.exception.CommandErrorException;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

import java.util.List;

public class NumberParameterType implements ParameterType<BukkitCommandActor, Integer> {

    private final List<Integer> allowedNumbers = List.of(1, 2, 3, 4, 5, 10, 20, 32, 64);

    @Override
    public Integer parse(MutableStringStream input, ExecutionContext<BukkitCommandActor> context) {
        String str = input.readString();
        try {
            int number = Integer.parseInt(str);
            if (!allowedNumbers.contains(number)) {
                throw new CommandErrorException("Invalid number: " + number);
            }
            return number;
        } catch (NumberFormatException e) {
            throw new CommandErrorException("Not a valid number: " + str);
        }
    }

    @Override
    public SuggestionProvider<BukkitCommandActor> defaultSuggestions() {
        return context -> allowedNumbers.stream()
                .map(String::valueOf)
                .toList();
    }
}
