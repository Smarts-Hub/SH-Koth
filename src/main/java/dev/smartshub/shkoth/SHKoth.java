package dev.smartshub.shkoth;

import dev.smartshub.shkoth.api.KothAPIProvider;
import dev.smartshub.shkoth.koth.registry.KothRegistry;
import dev.smartshub.shkoth.storage.config.service.ConfigService;
import revxrsal.zapper.ZapperJavaPlugin;

public class SHKoth extends ZapperJavaPlugin {

    private KothRegistry kothRegistry;
    private KothAPIImpl kothAPI;

    private ConfigService configService;

    @Override
    public void onEnable() {
        getLogger().info("SHKoth has been enabled!");
        setUpConfig();
        initAPI();
    }

    @Override
    public void onDisable() {
        getLogger().info("SHKoth has been disabled!");
        KothAPIProvider.unload();
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
