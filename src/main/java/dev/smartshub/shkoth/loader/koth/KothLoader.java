package dev.smartshub.shkoth.loader.koth;

import dev.smartshub.shkoth.api.config.ConfigContainer;
import dev.smartshub.shkoth.api.koth.Koth;
import dev.smartshub.shkoth.api.loader.Loader;
import dev.smartshub.shkoth.api.team.track.TeamTracker;
import dev.smartshub.shkoth.builder.KothBuilder;
import dev.smartshub.shkoth.service.config.ConfigService;

import java.util.HashSet;
import java.util.Set;

public class KothLoader implements Loader<Set<Koth>> {

    private final KothBuilder kothBuilder;
    private final ConfigService configService;
    private final TeamTracker teamTracker;

    public KothLoader(ConfigService configService, TeamTracker teamTracker) {
        this.configService = configService;
        this.teamTracker = teamTracker;
        this.kothBuilder = new KothBuilder(teamTracker);
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
