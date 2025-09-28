package dev.smartshub.shkoth.api;

import org.jetbrains.annotations.NotNull;

public final class KothAPIProvider {

    private static volatile KothAPI instance;

    @NotNull
    public static KothAPI getInstance() {
        KothAPI api = instance;
        if (api == null) {
            throw new IllegalStateException("KothAPI not initialized yet! Is SH-Koth plugin loaded?");
        }
        return api;
    }

    public static boolean isAvailable() {
        return instance != null;
    }

    public static void setInstance(@NotNull KothAPI api) {
        if (instance != null) {
            throw new IllegalStateException("KothAPI already initialized!");
        }
        instance = api;
    }

    public static void unload() {
        instance = null;
    }

    private KothAPIProvider() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}