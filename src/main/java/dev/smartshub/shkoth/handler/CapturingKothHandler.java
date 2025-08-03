package dev.smartshub.shkoth.handler;

import dev.smartshub.shkoth.api.model.koth.Koth;
import dev.smartshub.shkoth.registry.KothRegistry;

public class CapturingKothHandler {

    private final KothRegistry kothRegistry;

    public CapturingKothHandler(KothRegistry kothRegistry) {
        this.kothRegistry = kothRegistry;
    }

    public void handleTickForCapturedKoths() {
        for(Koth koth : kothRegistry.getRunningKoths()){
            koth.tick();
        }
    }

}
