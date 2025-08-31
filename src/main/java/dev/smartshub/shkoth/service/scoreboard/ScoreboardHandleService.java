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

    public void handleChange(Koth koth, KothState newState) {
        World world = Bukkit.getWorld(koth.getArea().worldName());
        if (world == null) return;
        this.handleChange(world, koth, newState);
    }

    public void handleChange(World world, Koth koth, KothState newState) {
        world.getPlayers().forEach(player -> {
            this.handleChange(player, koth, newState);
        });
    }

    public void handleChange(Player player, Koth koth, KothState state) {
        if(state == KothState.INACTIVE) {
            service.remove(player);
        }

        service.send(player, koth, state);
    }

    public void handle(Koth koth, KothState state) {
        service.updateAll(koth, state);
    }

    public void handleAll() {
        for (Koth koth : kothRegistry.getRunning()) {
            if(!koth.isScoreboardEnabled()) continue;
            handle(koth, koth.getState());
        }
    }
}

