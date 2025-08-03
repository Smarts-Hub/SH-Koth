package dev.smartshub.shkoth.api.core;

public final class KothAPIProvider {
    private static KothAPI instance;
    
    public static KothAPI getInstance() {
        if (instance == null) {
            throw new IllegalStateException("KothAPI not initialized yet!");
        }
        return instance;
    }
    
    public static void setInstance(KothAPI api) {
        if (instance != null) {
            throw new IllegalStateException("KothAPI already initialized!");
        }
        instance = api;
    }
    
    public static void unload() {
        instance = null;
    }
}