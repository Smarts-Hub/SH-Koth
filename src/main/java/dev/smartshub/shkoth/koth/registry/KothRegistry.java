package dev.smartshub.shkoth.koth.registry;

import dev.smartshub.shkoth.api.model.koth.Koth;
import dev.smartshub.shkoth.koth.loader.KothLoader;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class KothRegistry {

    private final KothLoader kothLoader = new KothLoader();

    private final Set<Koth> koths;

    public KothRegistry() {
        this.koths = kothLoader.loadKoths();
    }

    public void registerKoth(Koth koth) {
        koths.add(koth);
    }

    public void unregisterKoth(String kothId) {
        koths.removeIf(koth -> koth.getId().equalsIgnoreCase(kothId));
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

    public Set<Koth> getAllKoths() {
        return Set.copyOf(koths);
    }

    public Set<Koth> getRunningKoths() {
        return koths.stream()
                .filter(Koth::isRunning)
                .collect(Collectors.toSet());
    }

    public Koth getKothByPlayer(UUID playerID) {
        return koths.stream()
                .filter(koth -> koth.getPlayersInside().contains(playerID))
                .findFirst()
                .orElse(null);
    }

    public boolean startKoth(String kothId) {
        Koth koth = getKoth(kothId);
        if (koth != null && !koth.isRunning()) {
            koth.start();
            return true;
        }
        return false;
    }

    public boolean stopKoth(String kothId) {
        Koth koth = getKoth(kothId);
        if (koth != null && koth.isRunning()) {
            koth.stop();
            return true;
        }
        return false;
    }

}
