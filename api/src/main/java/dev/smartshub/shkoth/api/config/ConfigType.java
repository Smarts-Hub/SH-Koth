package dev.smartshub.shkoth.api.config;

public enum ConfigType {

    DATABASE("configuration/database.yml", "database.yml", "configuration"),
    HOOKS("configuration/hooks.yml", "hooks.yml", "configuration"),
    MESSAGES("lang/messages.yml", "messages.yml", "lang"),
    BROADCAST("lang/broadcast.yml", "broadcast.yml", "lang"),
    BOSSBAR("lang/bossbar.yml", "bossbar.yml", "lang"),
    ACTIONBAR("lang/actionbar.yml", "actionbar.yml", "lang"),
    SOUND("lang/sound.yml", "sound.yml", "lang"),
    TITLE("lang/title.yml", "title.yml", "lang"),

    KOTH_DEFINITION("koths/", null, "koths");

    private final String defaultPath;
    private final String resourceName;
    private final String parentFolder;

    ConfigType(String defaultPath, String resourceName, String parentFolder) {
        this.defaultPath = defaultPath;
        this.resourceName = resourceName;
        this.parentFolder = parentFolder;
    }

    public String getDefaultPath() {
        return defaultPath;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getParentFolder() {
        return parentFolder;
    }

    public boolean isFolder() {
        return defaultPath.endsWith("/");
    }
}
