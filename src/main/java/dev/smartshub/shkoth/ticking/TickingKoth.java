package dev.smartshub.shkoth.ticking;

import dev.smartshub.shkoth.api.model.koth.Koth;
import dev.smartshub.shkoth.registry.KothRegistry;

public class TickingKoth {

    private final KothRegistry kothRegistry;

    public TickingKoth(KothRegistry kothRegistry) {
        this.kothRegistry = kothRegistry;
    }

    public void handleTickForCapturedKoths() {
        for(Koth koth : kothRegistry.getRunningKoths()){
            koth.tick();
        }
    }

}
