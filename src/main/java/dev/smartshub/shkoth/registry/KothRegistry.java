package dev.smartshub.shkoth.registry;

import dev.smartshub.shkoth.builder.KothBuilder;
import dev.smartshub.shkoth.koth.Koth;

import java.util.Set;

public class KothRegistry {

    private final Set<Koth> koths;

    public KothRegistry() {
        KothBuilder kothBuilder = new KothBuilder();
        this.koths = kothBuilder.loadKothsFromConfig();
    }

    public void registerKoth(Koth koth) {
        koths.add(koth);
    }

    public void unregisterKoth(Koth koth) {
        koths.remove(koth);
    }

    public Koth getKoth(String id) {
        return koths.stream()
                .filter(koth -> koth.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

}
