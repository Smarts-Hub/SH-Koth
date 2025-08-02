package dev.smartshub.shkoth.model.koth.guideline;

public enum KothState {
    INACTIVE,
    RUNNING;

    public static KothState fromString(String state) {
        try {
            return KothState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            return INACTIVE;
        }
    }
}
