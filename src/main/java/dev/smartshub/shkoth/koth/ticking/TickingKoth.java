package dev.smartshub.shkoth.koth.ticking;

import dev.smartshub.shkoth.api.model.koth.Koth;
import dev.smartshub.shkoth.koth.registry.KothRegistry;

public class TickingKoth {

    private final KothRegistry kothRegistry;

    public TickingKoth(KothRegistry kothRegistry) {
        this.kothRegistry = kothRegistry;
    }

    public void handleTickForCapturedKoths() {
        for(Koth koth : kothRegistry.getRunning()){
            koth.tick();
        }
    }

}
