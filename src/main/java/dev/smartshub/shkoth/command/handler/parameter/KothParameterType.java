package dev.smartshub.shkoth.command.handler.parameter;

import dev.smartshub.shkoth.api.koth.Koth;
import dev.smartshub.shkoth.registry.KothRegistry;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.exception.CommandErrorException;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

public class KothParameterType implements ParameterType<BukkitCommandActor, Koth> {

    private final KothRegistry kothRegistry;

    public KothParameterType(KothRegistry kothRegistry) {
        this.kothRegistry = kothRegistry;
    }

    @Override
    public Koth parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<BukkitCommandActor> context) {
        String name = input.readString();
        Koth koth = kothRegistry.get(name);
        if (koth == null) {
            throw new CommandErrorException("No such KOTH: " + name);
        }
        return koth;
    }

    @Override
    public @NotNull SuggestionProvider<BukkitCommandActor> defaultSuggestions() {
        return context -> kothRegistry.getAll().stream()
                .map(Koth::getId)
                .toList();
    }
}
