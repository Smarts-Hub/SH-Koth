package dev.smartshub.shkoth.loader.config;

import dev.smartshub.shkoth.SHKoth;
import dev.smartshub.shkoth.api.config.ConfigContainer;
import dev.smartshub.shkoth.api.config.ConfigException;
import dev.smartshub.shkoth.api.config.ConfigType;
import dev.smartshub.shkoth.storage.config.ConfigContainerImpl;
import dev.smartshub.shkoth.storage.config.Configuration;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigLoader {
    
    private final SHKoth plugin;
    private final Map<String, Configuration> configCache = new ConcurrentHashMap<>();
    private final Map<String, Long> lastModified = new ConcurrentHashMap<>();
    private final boolean cacheEnabled;
    
    public ConfigLoader(SHKoth plugin) {
        this(plugin, true);
    }
    
    public ConfigLoader(SHKoth plugin, boolean cacheEnabled) {
        this.plugin = plugin;
        this.cacheEnabled = cacheEnabled;
    }
    
    public ConfigContainer load(String fileName, ConfigType type) {
        String normalizedName = normalizeFileName(fileName);
        
        if (cacheEnabled && isCacheValid(normalizedName)) {
            Configuration cachedConfig = configCache.get(normalizedName);
            return new ConfigContainerImpl(normalizedName, cachedConfig, type);
        }
        
        Configuration config = loadFromFile(normalizedName);
        
        if (cacheEnabled) {
            configCache.put(normalizedName, config);
            updateLastModified(normalizedName);
        }
        
        return new ConfigContainerImpl(normalizedName, config, type);
    }
    
    public void reload(String fileName, ConfigType type) {
        String normalizedName = normalizeFileName(fileName);
        
        try {
            Configuration config = configCache.get(normalizedName);
            if (config != null) {
                config.reloadFile();
                updateLastModified(normalizedName);
            } else {
                load(fileName, type);
            }
        } catch (Exception e) {
            throw new ConfigException.ConfigLoadException(normalizedName, e);
        }
    }
    
    public void save(String fileName) {
        String normalizedName = normalizeFileName(fileName);
        Configuration config = configCache.get(normalizedName);
        
        if (config == null) {
            throw new ConfigException.ConfigNotFoundException(normalizedName);
        }
        
        try {
            config.saveFile();
            updateLastModified(normalizedName);
        } catch (Exception e) {
            throw new ConfigException.ConfigSaveException(normalizedName, e);
        }
    }
    
    public Set<ConfigContainer> loadFromFolder(String folderName, ConfigType type) {
        Set<ConfigContainer> containers = new HashSet<>();
        File folder = new File(plugin.getDataFolder(), folderName);
        
        if (!folder.exists() || !folder.isDirectory()) {
            plugin.getLogger().warning("Folder '" + folderName + "' does not exist or is not a directory.");
            return containers;
        }
        
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return containers;
        
        for (File file : files) {
            try {
                String fileName = file.getName();
                Configuration config = new Configuration(plugin, file, fileName);
                config.load(file);
                
                if (cacheEnabled) {
                    configCache.put(fileName, config);
                    lastModified.put(fileName, file.lastModified());
                }
                
                containers.add(new ConfigContainerImpl(fileName, config, type));
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to load config: " + file.getName());
                e.printStackTrace();
            }
        }
        
        return containers;
    }
    
    public void ensureFolderExists(String folderName) {
        File folder = new File(plugin.getDataFolder(), folderName);
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (!created) {
                throw new ConfigException("Cannot create folder: " + folderName);
            }
        }
    }
    
    public void clearCache() {
        configCache.clear();
        lastModified.clear();
    }
    
    public void evictFromCache(String fileName) {
        String normalizedName = normalizeFileName(fileName);
        configCache.remove(normalizedName);
        lastModified.remove(normalizedName);
    }
    
    private Configuration loadFromFile(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        Configuration config = new Configuration(plugin, file, fileName);
        
        try {
            config.load(file);
            return config;
        } catch (Exception e) {
            throw new ConfigException.ConfigLoadException(fileName, e);
        }
    }

    private boolean isCacheValid(String fileName) {
        if (!configCache.containsKey(fileName)) {
            return false;
        }
        
        File file = new File(plugin.getDataFolder(), fileName);
        Long cachedTime = lastModified.get(fileName);
        
        return cachedTime != null && cachedTime >= file.lastModified();
    }
    
    private void updateLastModified(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        lastModified.put(fileName, file.lastModified());
    }
    
    private String normalizeFileName(String name) {
        return name.endsWith(".yml") ? name : name + ".yml";
    }
}