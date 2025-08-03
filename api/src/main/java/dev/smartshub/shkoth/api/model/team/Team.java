package dev.smartshub.shkoth.api.model.team;

import java.util.Set;
import java.util.UUID;

public record Team(
        Set<UUID> members,
        int size
) {
}
