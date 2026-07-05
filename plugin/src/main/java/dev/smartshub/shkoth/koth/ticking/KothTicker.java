package dev.smartshub.shkoth.koth.ticking;

import dev.smartshub.shkoth.api.koth.Koth;
import dev.smartshub.shkoth.registry.KothRegistry;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class KothTicker {

    private final KothRegistry kothRegistry;

    public KothTicker(KothRegistry kothRegistry) {
        this.kothRegistry = kothRegistry;
    }

    public void handleTickForCapturedKoths() {
        for (Koth koth : kothRegistry.getRunning()) {
            try {
                koth.tick();
            } catch (Throwable t) {
                JavaPlugin.getProvidingPlugin(KothTicker.class).getLogger().log(
                        Level.SEVERE,
                        "KOTH '" + koth.getId() + "' threw during tick; isolated to avoid affecting other KOTHs",
                        t);
            }
        }
    }

}
