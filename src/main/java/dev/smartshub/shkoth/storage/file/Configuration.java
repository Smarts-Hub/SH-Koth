package dev.smartshub.shkoth.storage.file;

import dev.smartshub.shkoth.SHKoth;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Configuration extends YamlConfiguration {

    private final File file;
    private final SHKoth plugin;

    public Configuration(SHKoth plugin, File file, String fileName) {
        this.plugin = plugin;
        if (file != null && file.isDirectory()) {
            this.file = new File(file, fileName.endsWith(".yml") ? fileName : fileName + ".yml");
        } else {
            this.file = new File(plugin.getDataFolder(), fileName.endsWith(".yml") ? fileName : fileName + ".yml");
        }

        saveDefault();
        loadFile();
    }

    private void loadFile() {
        try {
            this.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void saveDefault() {
        if (!file.exists()) {
            plugin.saveResource(file.getName(), false);
        }
    }

    public void saveFile() {
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

