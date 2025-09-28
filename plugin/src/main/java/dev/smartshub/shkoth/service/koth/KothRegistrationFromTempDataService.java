package dev.smartshub.shkoth.service.koth;

import dev.smartshub.shkoth.api.config.ConfigContainer;
import dev.smartshub.shkoth.api.config.ConfigType;
import dev.smartshub.shkoth.api.koth.Koth;
import dev.smartshub.shkoth.api.team.track.TeamTracker;
import dev.smartshub.shkoth.builder.KothBuilder;
import dev.smartshub.shkoth.registry.KothRegistry;
import dev.smartshub.shkoth.service.config.ConfigService;
import dev.smartshub.shkoth.service.gui.menu.cache.KothTempData;
import dev.smartshub.shkoth.service.gui.menu.cache.KothToRegisterCache;
import dev.smartshub.shkoth.service.gui.menu.cache.KothValidation.ValidationResult;
import dev.smartshub.shkoth.service.gui.menu.cache.KothYamlSaver;

import java.io.File;
import java.util.UUID;

public class KothRegistrationFromTempDataService {

    private final KothToRegisterCache cache;
    private final ConfigService configService;
    private final KothRegistry kothRegistry;
    private final KothYamlSaver yamlSaver;
    private final TeamTracker teamTracker;
    private final String kothsFolderPath;

    public KothRegistrationFromTempDataService(KothToRegisterCache cache,
                                               ConfigService configService,
                                               KothRegistry kothRegistry,
                                               TeamTracker teamTracker,
                                               String kothsFolderPath) {
        this.cache = cache;
        this.configService = configService;
        this.kothRegistry = kothRegistry;
        this.teamTracker = teamTracker;
        this.kothsFolderPath = kothsFolderPath;
        this.yamlSaver = new KothYamlSaver(configService, kothsFolderPath);

    }

    public RegistrationResult registerKoth(UUID uuid) {
        try {

            KothTempData tempData = cache.getKothToRegister(uuid);
            if (tempData == null) {
                return new RegistrationResult(false, "Cant find temp data for: " + uuid);
            }

            ValidationResult validation = cache.validateKoth(uuid);
            if (!validation.valid()) {
                return new RegistrationResult(false, "Invalid data: " + String.join(", ", validation.errors()));
            }

            if (kothRegistry.get(tempData.getId()) != null) {
                return new RegistrationResult(false, "Koth existing yet with ID: " + tempData.getId());
            }


            boolean yamlSaved = yamlSaver.saveToYaml(tempData);
            if (!yamlSaved) {
                return new RegistrationResult(false, "Error saving the KOTH configuration");
            }


            String configPath = tempData.getId() + ".yml";

            ConfigContainer config = configService.provide(configPath, ConfigType.KOTH_DEFINITION);
            if (config == null) {
                return new RegistrationResult(false, "Error loading the KOTH configuration");
            }

            KothBuilder builder = new KothBuilder(teamTracker);
            Koth koth = builder.build(config);

            if (koth == null) {
                return new RegistrationResult(false, "Error building the KOTH");
            }

            kothRegistry.register(koth);

            cache.removeKothToRegister(uuid);

            return new RegistrationResult(true, "KOTH '" + tempData.getDisplayName() + "' registered successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            return new RegistrationResult(false, "Unexpected error: " + e.getMessage());
        }
    }

    public KothPreview getKothPreview(UUID uuid) {
        KothTempData tempData = cache.getKothToRegister(uuid);
        if (tempData == null) {
            return null;
        }

        ValidationResult validation = cache.validateKoth(uuid);

        return new KothPreview(
                tempData.getId(),
                tempData.getDisplayName(),
                tempData.getMaxTime(),
                tempData.getCaptureTime(),
                tempData.getWorldName(),
                0,
                validation.valid(),
                validation.errors()
        );
    }

    public boolean cancelRegistration(UUID uuid) {
        if (cache.hasKothToRegister(uuid)) {
            cache.removeKothToRegister(uuid);
            return true;
        }
        return false;
    }

    public record RegistrationResult(boolean success, String message) {}

    public record KothPreview(
            String id,
            String displayName,
            int maxTime,
            int captureTime,
            String worldName,
            int schedulesCount,
            boolean isValid,
            java.util.List<String> validationErrors
    ) {}
}