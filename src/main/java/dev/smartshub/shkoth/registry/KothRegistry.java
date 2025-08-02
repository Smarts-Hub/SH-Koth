package dev.smartshub.shkoth.registry;

import dev.smartshub.shkoth.koth.Koth;
import dev.smartshub.shkoth.loader.KothLoader;

import java.util.Set;

public class KothRegistry {

    private final KothLoader kothLoader = new KothLoader();

    private final Set<Koth> koths;

    public KothRegistry() {
        this.koths = kothLoader.loadKoths();
    }

    public void registerKoth(Koth koth) {
        koths.add(koth);
    }

    public void unregisterKoth(Koth koth) {
        koths.remove(koth);
    }

    public void reloadKoths() {
        koths.clear();
        koths.addAll(kothLoader.loadKoths());
    }

    public Koth getKoth(String id) {
        return koths.stream()
                .filter(koth -> koth.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

}
