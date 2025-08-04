package dev.smartshub.shkoth.koth.loader;

import dev.smartshub.shkoth.api.model.config.ConfigContainer;
import dev.smartshub.shkoth.api.model.koth.Koth;
import dev.smartshub.shkoth.koth.builder.KothBuilder;
import dev.smartshub.shkoth.storage.config.service.ConfigService;

import java.util.HashSet;
import java.util.Set;

public class KothLoader {

    private final KothBuilder kothBuilder = new KothBuilder();
    private final ConfigService configService;

    public KothLoader(ConfigService configService) {
        this.configService = configService;
    }

    public Set<Koth> loadKoths(){
        Set<Koth> koths = new HashSet<>();

        for (ConfigContainer config : configService.provideAllKoths()) {
            koths.add(kothBuilder.buildKothFromFile(config));
        }

        return koths;
    }

}
