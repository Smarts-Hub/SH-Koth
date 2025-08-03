package dev.smartshub.shkoth;

import dev.smartshub.shkoth.api.core.Koth;
import dev.smartshub.shkoth.api.core.KothAPI;
import dev.smartshub.shkoth.registry.KothRegistry;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

public class KothAPIImpl implements KothAPI {
    
    private final KothRegistry kothRegistry;
    
    public KothAPIImpl(KothRegistry kothManager) {
        this.kothRegistry = kothManager;
    }
    
    @Override
    public @Nullable Koth getKoth(@NotNull String id) {
        return kothRegistry.getKoth(id);
    }
    
    @Override
    public @NotNull Collection<Koth> getAllKoths() {
        return kothRegistry.getAllKoths();
    }
    
    @Override
    public @NotNull Collection<Koth> getRunningKoths() {
        return kothRegistry.getRunningKoths();
    }
    
    @Override
    public Optional<Koth> getKothByPlayer(@NotNull Player player) {
        return Optional.ofNullable(kothRegistry.getKothByPlayer(player.getUniqueId()));
    }

    
    @Override
    public boolean startKoth(@NotNull String kothId) {
        return kothRegistry.startKoth(kothId);
    }
    
    @Override
    public boolean stopKoth(@NotNull String kothId) {
        return kothRegistry.stopKoth(kothId);
    }
    
    @Override
    public void registerKoth(@NotNull Koth koth) {
        kothRegistry.registerKoth(koth);
    }
    
    @Override
    public void unregisterKoth(@NotNull String kothId) {
        kothRegistry.unregisterKoth(kothId);
    }
}