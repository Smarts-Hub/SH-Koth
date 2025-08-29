package dev.smartshub.shkoth.api.config;

public enum ConfigType {
    DATABASE("configuration/database.yml"),
    MESSAGES("lang/messages.yml"),
    BROADCAST("lang/broadcast.yml"),
    KOTH_DEFINITION("koths/"),
    HOOKS("configuration/hooks.yml");

    private final String defaultPath;

    ConfigType(String defaultPath) {
        this.defaultPath = defaultPath;
    }

    public String getDefaultPath() {
        return defaultPath;
    }

    public boolean isFolder() {
        return defaultPath.endsWith("/");
    }

    public String getParentFolder() {
        if (isFolder()) {
            return defaultPath.substring(0, defaultPath.length() - 1);
        }

        int lastSlash = defaultPath.lastIndexOf('/');
        return lastSlash > 0 ? defaultPath.substring(0, lastSlash) : null;
    }

    public String getFileName() {
        if (isFolder()) {
            return null;
        }

        int lastSlash = defaultPath.lastIndexOf('/');
        return lastSlash >= 0 ? defaultPath.substring(lastSlash + 1) : defaultPath;
    }

}