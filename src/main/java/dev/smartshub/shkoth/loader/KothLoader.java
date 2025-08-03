package dev.smartshub.shkoth.loader;

import dev.smartshub.shkoth.api.core.Koth;
import dev.smartshub.shkoth.builder.KothBuilder;
import dev.smartshub.shkoth.storage.file.Configuration;
import dev.smartshub.shkoth.storage.file.FileManager;

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
