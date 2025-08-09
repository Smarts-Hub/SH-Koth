package dev.smartshub.shkoth;

import dev.smartshub.shkoth.api.KothAPIProvider;
import dev.smartshub.shkoth.api.model.koth.guideline.KothType;
import dev.smartshub.shkoth.registry.KothRegistry;
import dev.smartshub.shkoth.api.model.koth.tally.TallyFactory;
import dev.smartshub.shkoth.koth.tally.capture.CaptureTally;
import dev.smartshub.shkoth.koth.tally.score.ScoreTally;
import dev.smartshub.shkoth.service.config.ConfigService;
import revxrsal.zapper.ZapperJavaPlugin;

public class SHKoth extends ZapperJavaPlugin {

    private KothRegistry kothRegistry;
    private KothAPIImpl kothAPI;

    private ConfigService configService;

    @Override
    public void onEnable() {
        getLogger().info("SHKoth has been enabled!");
        factoryRegister();
        setUpConfig();
        initAPI();
    }

    @Override
    public void onDisable() {
        getLogger().info("SHKoth has been disabled!");
        KothAPIProvider.unload();
    }

    private void factoryRegister(){
        TallyFactory.register(KothType.CAPTURE, CaptureTally::new);
        TallyFactory.register(KothType.SCORE, ScoreTally::new);

    }

    private void initAPI() {
        kothRegistry = new KothRegistry(configService);
        kothAPI = new KothAPIImpl(kothRegistry);
        KothAPIProvider.setInstance(kothAPI);
    }

    private void setUpConfig(){
        configService = new ConfigService(this);
    }

    //TODO: commands, listeners, koth-timer logic, messages
}
