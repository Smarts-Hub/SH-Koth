package dev.smartshub.shkoth.storage.file;

import dev.smartshub.shkoth.SHKoth;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FileManager {

    private static final Map<String, Configuration> configs = new HashMap<>();
    private static SHKoth plugin;

    public static void init(SHKoth pluginInstance) {
        plugin = pluginInstance;
        load("config");
        load("lang");
        createKothsFolder();
    }

    private static void load(String name) {
        String fileName = fixName(name);
        File file = new File(plugin.getDataFolder(), fileName);
        Configuration config = new Configuration(plugin, file, fileName);

        try {
            config.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        configs.put(fileName, config);
    }

    public static void reload(String name) {
        String fileName = fixName(name);
        Configuration config = configs.get(fileName);
        if (config != null) {
            config.reloadFile();
        }
    }

    public static void save(String name) {
        String fileName = fixName(name);
        Configuration config = configs.get(fileName);
        if (config != null) {
            config.saveFile();
        }
    }

    public static Configuration get(String name) {
        return configs.get(fixName(name));
    }

    private static String fixName(String name) {
        return name.endsWith(".yml") ? name : name + ".yml";
    }

    public static void createKothsFolder() {
        File schemsFolder = new File(plugin.getDataFolder(), "koths");
        if (!schemsFolder.exists()) {
            boolean created = schemsFolder.mkdirs();
            if (!created) {
                plugin.getLogger().severe("Can not create 'koths' folder!");
            }
        }
    }

    public static Set<Configuration> getAllFromFolder(String folderName) {
        Set<Configuration> configurationSet = new HashSet<>();
        File folder = new File(plugin.getDataFolder(), folderName);

        if (!folder.exists() || !folder.isDirectory()) {
            plugin.getLogger().warning("Folder '" + folderName + "' does not exist or is not a directory.");
            return configurationSet;
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return configurationSet;

        for (File file : files) {
            String fileName = file.getName();
            Configuration config = new Configuration(plugin, file, fileName);
            try {
                config.load(file);
                configurationSet.add(config);
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to load config: " + fileName);
                e.printStackTrace();
            }
        }

        return configurationSet;
    }


}

