package dev.smartshub.shkoth.command.handler.suggestion;

import dev.smartshub.shkoth.api.koth.Koth;
import dev.smartshub.shkoth.registry.KothRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

import java.util.stream.Stream;

public class CommandSuggestionProvider {

    private final KothRegistry kothRegistry;

    public CommandSuggestionProvider(KothRegistry kothRegistry) {
        this.kothRegistry = kothRegistry;
    }

    public SuggestionProvider<BukkitCommandActor> getKothProvider() {
        return context -> kothRegistry.getAll().stream()
                .map(Koth::getId)
                .toList();
    }

    public SuggestionProvider<BukkitCommandActor> getPlayerProvider() {
        return context -> Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .toList();
    }

    public SuggestionProvider<BukkitCommandActor> getNumberProvider() {
        return context -> Stream.of(1, 2, 3, 4, 5, 10, 20, 32, 64)
                .map(String::valueOf)
                .toList();
    }

}