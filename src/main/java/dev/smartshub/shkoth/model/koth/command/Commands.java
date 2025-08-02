package dev.smartshub.shkoth.model.koth.command;

import java.util.List;

public record Commands(
        List<String> startCommands,
        List<String> endCommands,
        List<String> winnersCommands
) {
}
