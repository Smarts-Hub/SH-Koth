package dev.smartshub.shkoth.api.model.koth.guideline;

public enum KothType {
    CAPTURE,
    SCORE;

    public static KothType fromString(String mode) {
        try {
            return KothType.valueOf(mode.toUpperCase());
        } catch (IllegalArgumentException e) {
            return CAPTURE;
        }
    }

}
