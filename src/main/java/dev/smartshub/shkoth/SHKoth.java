package dev.smartshub.shkoth;

import dev.smartshub.shkoth.api.KothAPIProvider;
import dev.smartshub.shkoth.koth.registry.KothRegistry;
import dev.smartshub.shkoth.storage.config.FileManager;
import revxrsal.zapper.ZapperJavaPlugin;

public class SHKoth extends ZapperJavaPlugin {

    private KothRegistry kothRegistry;
    private KothAPIImpl kothAPI;

    @Override
    public void onEnable() {
        getLogger().info("SHKoth has been enabled!");

        kothRegistry = new KothRegistry();
        kothAPI = new KothAPIImpl(kothRegistry);
        KothAPIProvider.setInstance(kothAPI);

        FileManager.init(this);
    }

    @Override
    public void onDisable() {
        getLogger().info("SHKoth has been disabled!");
        KothAPIProvider.unload();
    }

    //TODO: commands, listeners, koth-timer logic, messages
}
