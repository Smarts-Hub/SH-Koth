package dev.smartshub.shkoth.koth.loader;

import dev.smartshub.shkoth.api.model.config.ConfigContainer;
import dev.smartshub.shkoth.api.model.koth.Koth;
import dev.smartshub.shkoth.api.model.loader.Loader;
import dev.smartshub.shkoth.koth.builder.KothBuilder;
import dev.smartshub.shkoth.storage.config.service.ConfigService;

import java.util.HashSet;
import java.util.Set;

public class KothLoader implements Loader<Set<Koth>> {

    private final KothBuilder kothBuilder = new KothBuilder();
    private final ConfigService configService;

    public KothLoader(ConfigService configService) {
        this.configService = configService;
    }

    @Override
    public Set<Koth> load(){
        Set<Koth> koths = new HashSet<>();

        for (ConfigContainer config : configService.provideAllKoths()) {
            koths.add(kothBuilder.build(config));
        }

        return koths;
    }

}
