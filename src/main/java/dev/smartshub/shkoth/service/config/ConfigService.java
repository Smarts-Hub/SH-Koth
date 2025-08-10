package dev.smartshub.shkoth.service.config;

import dev.smartshub.shkoth.SHKoth;
import dev.smartshub.shkoth.api.config.ConfigContainer;
import dev.smartshub.shkoth.api.config.ConfigException;
import dev.smartshub.shkoth.api.config.ConfigType;
import dev.smartshub.shkoth.loader.config.ConfigLoader;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public class ConfigService {
    
    private final ConfigLoader loader;
    private final Map<ConfigType, String> typeToPath = new EnumMap<>(ConfigType.class);
    
    public ConfigService(SHKoth plugin) {
        this.loader = new ConfigLoader(plugin);
        initializeTypePaths();
        initialize();
    }
    
    public void initialize() {
        loader.ensureFolderExists("koths");
        
        provide(ConfigType.DATABASE);
        provide(ConfigType.MESSAGES);
    }
    
    public ConfigContainer provide(ConfigType type) {
        String path = typeToPath.get(type);
        return loader.load(path, type);
    }
    
    public ConfigContainer provide(String customPath, ConfigType type) {
        return loader.load(customPath, type);
    }
    
    public Set<ConfigContainer> provideAllKoths() {
        return loader.loadFromFolder("koths", ConfigType.KOTH_DEFINITION);
    }
    
    public void reload(ConfigType type) {
        String path = typeToPath.get(type);
        loader.reload(path, type);
    }
    
    public void save(ConfigType type) {
        String path = typeToPath.get(type);
        loader.save(path);
    }
    
    public void clearCache() {
        loader.clearCache();
    }
    
    public void validateConfiguration(ConfigContainer config) throws ConfigException {
        switch (config.getType()) {
            case DATABASE -> validateDatabaseConfig(config);
            case MESSAGES -> validateMessagesConfig(config);
            case KOTH_DEFINITION -> validateKothConfig(config);
        }
    }
    
    private void validateDatabaseConfig(ConfigContainer config) {
        config.requirePath("host");
        config.requirePath("password");
        config.requirePath("port");
        config.requirePath("username");
        config.requirePath("db-name");
        config.requirePath("driver");
    }
    
    private void validateMessagesConfig(ConfigContainer config) {
        config.requirePath("messages");
    }
    
    private void validateKothConfig(ConfigContainer config) {
        config.requirePath("corner-2");
        config.requirePath("corner-1");
        config.requirePath("display-name");
        config.requirePath("max-duration");
    }
    
    private void initializeTypePaths() {
        for (ConfigType type : ConfigType.values()) {
            typeToPath.put(type, type.getDefaultPath());
        }
    }
}
