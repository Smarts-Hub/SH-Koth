package dev.smartshub.shkoth.storage.config;

import dev.smartshub.shkoth.SHKoth;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Configuration extends YamlConfiguration {

    private final File file;
    private final SHKoth plugin;
    private boolean allowSave = true;

    public Configuration(SHKoth plugin, File file, String fileName) {
        this.plugin = plugin;

        if (file == null) {
            this.file = new File(plugin.getDataFolder(), fileName.endsWith(".yml") ? fileName : fileName + ".yml");
        } else {
            if (file.isDirectory()) {
                this.file = new File(file, fileName.endsWith(".yml") ? fileName : fileName + ".yml");
            } else {
                this.file = file;
            }
        }

        if (this.file.getParentFile() != null) {
            this.file.getParentFile().mkdirs();
        }

        loadFile();
    }

    public void setAllowSave(boolean allowSave) {
        this.allowSave = allowSave;
    }

    private void loadFile() {
        try {
            this.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void saveFile() {
        if (!allowSave) {
            plugin.getLogger().warning("Cannot save " + file.getName() + " - this config should be edited manually to preserve formatting.");
            return;
        }

        try {
            this.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadFile() {
        try {
            loadFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}