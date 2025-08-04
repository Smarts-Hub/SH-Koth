package dev.smartshub.shkoth.api.model.config;

public enum ConfigType {
    DATABASE("database.yml"),
    MESSAGES("messages.yml"),
    KOTH_DEFINITION("koths/"),
    PLUGIN_CONFIG("config.yml");
    
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
}