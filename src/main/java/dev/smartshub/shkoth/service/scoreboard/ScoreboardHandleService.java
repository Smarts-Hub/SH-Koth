package dev.smartshub.shkoth.service.scoreboard;

import dev.smartshub.shkoth.api.koth.Koth;
import dev.smartshub.shkoth.api.koth.guideline.KothState;
import dev.smartshub.shkoth.registry.KothRegistry;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class ScoreboardHandleService {

    private final SendScoreboardService service;
    private final KothRegistry kothRegistry;

    public ScoreboardHandleService(SendScoreboardService service, KothRegistry kothRegistry) {
        this.service = service;
        this.kothRegistry = kothRegistry;
    }

    public void handleChange(Koth koth) {
        World world = Bukkit.getWorld(koth.getArea().worldName());
        if (world == null) return;
        this.handleChange(world, koth);
    }

    public void handleChange(World world, Koth koth) {
        world.getPlayers().forEach(player -> {
            this.handleChange(player, koth);
        });
    }

    public void handleChange(Player player, Koth koth) {
        if(koth.getState() == KothState.INACTIVE) {
            service.remove(player);
        }

        service.send(player, koth);
    }

    public void handle(Koth koth) {
        service.updateAll(koth);
    }

    public void handleAll() {
        for (Koth koth : kothRegistry.getRunning()) {
            handle(koth);
        }
    }
}
