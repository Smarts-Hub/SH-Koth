package dev.smartshub.shkoth.koth.ticking;

import dev.smartshub.shkoth.api.koth.Koth;
import dev.smartshub.shkoth.registry.KothRegistry;

public class KothTicker {

    private final KothRegistry kothRegistry;

    public KothTicker(KothRegistry kothRegistry) {
        this.kothRegistry = kothRegistry;
    }

    public void handleTickForCapturedKoths() {
        for(Koth koth : kothRegistry.getRunning()){
            koth.tick();
        }
    }

}
