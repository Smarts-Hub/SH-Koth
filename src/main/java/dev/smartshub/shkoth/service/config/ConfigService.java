package dev.smartshub.shkoth.service.config;

import dev.smartshub.shkoth.SHKoth;
import dev.smartshub.shkoth.api.config.ConfigContainer;
import dev.smartshub.shkoth.api.config.ConfigException;
import dev.smartshub.shkoth.api.config.ConfigType;
import dev.smartshub.shkoth.loader.config.ConfigLoader;

import java.util.Set;

public class ConfigService {

    private final ConfigLoader loader;

    public ConfigService(SHKoth plugin) {
        this.loader = new ConfigLoader(plugin);
        initialize();
    }

    public void initialize() {
        loader.initializeAllFolders();

        provide(ConfigType.DATABASE);
        provide(ConfigType.MESSAGES);
        provide(ConfigType.BROADCAST);
        provide(ConfigType.HOOKS);
    }

    public ConfigContainer provide(ConfigType type) {
        if (type.isFolder()) {
            loader.loadFromFolder(type);
        }
        return loader.load(type);
    }

    public ConfigContainer provide(String customPath, ConfigType type) {
        return loader.load(customPath, type);
    }

    public Set<ConfigContainer> provideAllKoths() {
        return loader.loadFromFolder(ConfigType.KOTH_DEFINITION);
    }

    public void reload(ConfigType type) {
        if (type.isFolder()) {
            loader.evictFromCache(type);
        } else {
            loader.reload(type);
        }
    }

    public void save(ConfigType type) {
        if (type.isFolder()) {
            throw new IllegalArgumentException("Cannot save folder types directly.");
        }
        loader.save(type);
    }

    public void clearCache() {
        loader.clearCache();
    }

    public void validateConfiguration(ConfigContainer config) throws ConfigException {
        switch (config.getType()) {
            case DATABASE -> validateDatabaseConfig(config);
            case MESSAGES -> validateMessagesConfig(config);
            case BROADCAST -> validateBroadcastConfig(config);
            case KOTH_DEFINITION -> validateKothConfig(config);
            case HOOKS -> validateHooksConfig(config);
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

    private void validateBroadcastConfig(ConfigContainer config) {
        config.requirePath("broadcast");
    }

    private void validateKothConfig(ConfigContainer config) {
        config.requirePath("corner-2");
        config.requirePath("corner-1");
        config.requirePath("display-name");
        config.requirePath("max-duration");
    }

    private void validateHooksConfig(ConfigContainer config) {
    }

    public void reloadAll() {
        reload(ConfigType.DATABASE);
        reload(ConfigType.MESSAGES);
        reload(ConfigType.BROADCAST);
        reload(ConfigType.HOOKS);
    }

}