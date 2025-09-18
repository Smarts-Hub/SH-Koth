package dev.smartshub.shkoth.service.config;

import dev.dejvokep.boostedyaml.dvs.Pattern;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.smartshub.shkoth.SHKoth;
import dev.smartshub.shkoth.api.config.ConfigContainer;
import dev.smartshub.shkoth.api.config.ConfigException;
import dev.smartshub.shkoth.api.config.ConfigType;
import dev.smartshub.shkoth.loader.config.ConfigLoader;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.logging.Level;

public class ConfigService {

    private final ConfigLoader loader;
    private final SHKoth plugin;

    public ConfigService(SHKoth plugin) {
        this.plugin = plugin;
        this.loader = new ConfigLoader(plugin);
        initialize();
    }

    public void initialize() {
        updateConfigsIfNeeded();

        loader.initializeAllFolders();

        provide(ConfigType.DATABASE);
        provide(ConfigType.MESSAGES);
        provide(ConfigType.BROADCAST);
        provide(ConfigType.HOOKS);
        provide(ConfigType.ACTIONBAR);
        provide(ConfigType.TITLE);
        provide(ConfigType.SOUND);
        provide(ConfigType.BOSSBAR);
    }
    private void updateConfigsIfNeeded() {
        plugin.getDataFolder().mkdirs();

        updateConfigFile("config.yml");
        updateConfigFile("database.yml");
        updateConfigFile("messages.yml");
        updateConfigFile("broadcast.yml");
        updateConfigFile("hook.yml");
        updateConfigFile("actionbar.yml");
        updateConfigFile("title.yml");
        updateConfigFile("sound.yml");
        updateConfigFile("bossbar.yml");
    }

    private void updateConfigFile(String fileName) {
        try {
            File configFile = new File(plugin.getDataFolder(), fileName);
            InputStream defaultResource = plugin.getResource(fileName);

            if (defaultResource == null) {
                if (!configFile.exists()) {
                    plugin.getLogger().warning("Can't found default file for: " + fileName);
                }
                return;
            }

            YamlDocument config = YamlDocument.create(
                    configFile,
                    defaultResource,
                    GeneralSettings.DEFAULT,
                    LoaderSettings.builder()
                            .setAutoUpdate(true)
                            .build(),
                    DumperSettings.builder()
                            .setEncoding(DumperSettings.Encoding.UNICODE)
                            .build(),
                    UpdaterSettings.builder()
                            .setVersioning(new BasicVersioning("config-version"))
                            .setOptionSorting(UpdaterSettings.OptionSorting.SORT_BY_DEFAULTS)
                            .setKeepAll(true)
                            .build()
            );

            if (config.update()) {
                plugin.getLogger().info("Updated configuration: " + fileName);
                config.save();
            }

        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Error updating config: " + fileName, e);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error processing: " + fileName, e);
        }
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
        updateConfigsIfNeeded();

        reload(ConfigType.DATABASE);
        reload(ConfigType.MESSAGES);
        reload(ConfigType.BROADCAST);
        reload(ConfigType.HOOKS);
        reload(ConfigType.ACTIONBAR);
        reload(ConfigType.TITLE);
        reload(ConfigType.SOUND);
        reload(ConfigType.BOSSBAR);
    }
}