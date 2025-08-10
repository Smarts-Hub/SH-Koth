package dev.smartshub.shkoth.api.koth.guideline;

public enum KothState {
    INACTIVE,
    RUNNING,
    CAPTURING;

    public static KothState fromString(String state) {
        try {
            return KothState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            return INACTIVE;
        }
    }
}
