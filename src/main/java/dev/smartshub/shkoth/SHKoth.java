package dev.smartshub.shkoth;

import dev.smartshub.shkoth.storage.file.FileManager;
import revxrsal.zapper.ZapperJavaPlugin;

public class SHKoth extends ZapperJavaPlugin {

    // I don't like to this, might change it later
    public static SHKoth getInstance() {
        return ZapperJavaPlugin.getPlugin(SHKoth.class);
    }

    @Override
    public void onEnable() {
        getLogger().info("SHKoth has been enabled!");

        FileManager.init(this);
    }

    @Override
    public void onDisable() {
        getLogger().info("SHKoth has been disabled!");
    }

    //TODO: commands, listeners, koth-timer logic, messages

}
