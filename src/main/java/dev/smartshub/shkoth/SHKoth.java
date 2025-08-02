package dev.smartshub.shkoth;

import dev.smartshub.shkoth.storage.file.FileManager;
import revxrsal.zapper.ZapperJavaPlugin;

public class SHKoth extends ZapperJavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("SHKoth has been enabled!");

        FileManager.init(this);
    }

    @Override
    public void onDisable() {
        getLogger().info("SHKoth has been disabled!");
    }

}
