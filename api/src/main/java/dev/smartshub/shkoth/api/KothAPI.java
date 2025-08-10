package dev.smartshub.shkoth.api;

import dev.smartshub.shkoth.api.koth.Koth;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

public interface KothAPI {

    @Nullable
    Koth getKoth(@NotNull String id);

    @NotNull
    Collection<Koth> getAllKoths();

    @NotNull
    Collection<Koth> getRunningKoths();

    Optional<Koth> getKothByPlayer(@NotNull Player player);

    boolean startKoth(@NotNull String kothId);
    boolean stopKoth(@NotNull String kothId);
    void registerKoth(@NotNull Koth koth);
    void unregisterKoth(@NotNull String kothId);

}