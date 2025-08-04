package dev.smartshub.shkoth.koth.loader;

import dev.smartshub.shkoth.api.model.koth.Koth;
import dev.smartshub.shkoth.koth.builder.KothBuilder;
import dev.smartshub.shkoth.storage.config.Configuration;
import dev.smartshub.shkoth.storage.config.FileManager;

import java.util.HashSet;
import java.util.Set;

public class KothLoader {

    private final KothBuilder kothBuilder = new KothBuilder();

    public Set<Koth> loadKoths(){
        Set<Koth> koths = new HashSet<>();

        for (Configuration config : FileManager.getAllFromFolder("koths")) {
            koths.add(kothBuilder.buildKothFromFile(config));
        }

        return koths;
    }

}
