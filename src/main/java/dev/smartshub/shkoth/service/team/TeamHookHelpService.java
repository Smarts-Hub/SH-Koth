package dev.smartshub.shkoth.service.team;

import dev.smartshub.shkoth.api.config.ConfigContainer;

public class TeamHookHelpService {

    private final ConfigContainer configContainer;

    public TeamHookHelpService(ConfigContainer configContainer) {
        this.configContainer = configContainer;
    }

    public boolean isEnabled(String hook) {
        String path = "team." + hook + ".enabled";
        return configContainer.getBoolean(path, false);
    }

    public int getPriority(String hook) {
        String path = "team." + hook + ".priority";
        return configContainer.getInt(path, 0);
    }

}
