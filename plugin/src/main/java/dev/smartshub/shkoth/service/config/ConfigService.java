package dev.smartshub.shkoth.service.config;

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
        provide(ConfigType.SCHEDULERS);
        provide(ConfigType.BROADCAST);
        provide(ConfigType.HOOKS);
        provide(ConfigType.ACTIONBAR);
        provide(ConfigType.TITLE);
        provide(ConfigType.SOUND);
        provide(ConfigType.BOSSBAR);
        provide(ConfigType.DISCORD);
    }
    private void updateConfigsIfNeeded() {
        plugin.getDataFolder().mkdirs();

        String language = getLanguageSetting();
        File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists()) langFolder.mkdirs();
        File langCacheFile = new File(langFolder, ".lang_cache");
        String cachedLanguage = "";
        if (langCacheFile.exists()) {
            try {
                cachedLanguage = java.nio.file.Files.readString(langCacheFile.toPath()).trim();
            } catch (IOException ignored) {}
        }

        boolean languageChanged = !language.equalsIgnoreCase(cachedLanguage);
        if (languageChanged) {
            try {
                java.nio.file.Files.writeString(langCacheFile.toPath(), language);
            } catch (IOException ignored) {}
        }

        for (ConfigType configType : ConfigType.values()) {
            if (!configType.isFolder()) {
                updateConfigFileFromType(configType, languageChanged);
            }
        }

    }

    private void updateConfigFileFromType(ConfigType configType, boolean forceOverwriteLang) {
        if (configType.isFolder()) return;

        try {
            String resourcePath = configType.getDefaultPath();
            String fileName = configType.getResourceName();

            File folder = new File(plugin.getDataFolder(), configType.getParentFolder());
            if (!folder.exists()) folder.mkdirs();

            File configFile = new File(folder, fileName);

            if (configType.getParentFolder().equals("lang")) {
                String language = getLanguageSetting();
                if (language != null && !language.equalsIgnoreCase("en")) {
                    String extension = resourcePath.substring(resourcePath.lastIndexOf("."));
                    String baseName = resourcePath.substring(0, resourcePath.lastIndexOf("."));
                    String langResourcePath = baseName + "_" + language.toLowerCase() + extension;
                    if (plugin.getResource(langResourcePath) != null) {
                        resourcePath = langResourcePath;
                    }
                }

                if (forceOverwriteLang) {
                    try (InputStream in = plugin.getResource(resourcePath);
                         java.io.OutputStream out = new java.io.FileOutputStream(configFile)) {
                        if (in != null) {
                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = in.read(buffer)) != -1) {
                                out.write(buffer, 0, bytesRead);
                            }
                        }
                    } catch (IOException e) {
                        plugin.getLogger().warning("Failed to force overwrite lang file: " + fileName);
                    }
                }
            }

            InputStream defaultResource = plugin.getResource(resourcePath);

            if (defaultResource == null) {
                if (!configFile.exists()) {
                    plugin.getLogger().warning("Can't found default file for: " + resourcePath);
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
            plugin.getLogger().log(Level.SEVERE, "Error updating config: " + configType.getResourceName(), e);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error processing: " + configType.getResourceName(), e);
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
        reload(ConfigType.SCHEDULERS);
        reload(ConfigType.MESSAGES);
        reload(ConfigType.BROADCAST);
        reload(ConfigType.HOOKS);
        reload(ConfigType.ACTIONBAR);
        reload(ConfigType.TITLE);
        reload(ConfigType.SOUND);
        reload(ConfigType.BOSSBAR);
        provide(ConfigType.DISCORD);
    }

    private String getLanguageSetting() {
        File hooksFile = new File(new File(plugin.getDataFolder(), "configuration"), "hooks.yml");
        if (hooksFile.exists()) {
            org.bukkit.configuration.file.YamlConfiguration config = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(hooksFile);
            return config.getString("language", "en");
        }
        return "en";
    }
}