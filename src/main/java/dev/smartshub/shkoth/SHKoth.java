package dev.smartshub.shkoth;

import dev.smartshub.shkoth.api.core.KothAPIProvider;
import dev.smartshub.shkoth.registry.KothRegistry;
import dev.smartshub.shkoth.storage.file.FileManager;
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
    }

    //TODO: commands, listeners, koth-timer logic, messages

}
