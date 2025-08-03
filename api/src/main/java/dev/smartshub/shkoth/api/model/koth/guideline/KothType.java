package dev.smartshub.shkoth.api.model.koth.guideline;

public enum KothType {
    SOLO,
    TEAM;

    public static KothType fromString(String mode) {
        try {
            return KothType.valueOf(mode.toUpperCase());
        } catch (IllegalArgumentException e) {
            return SOLO;
        }
    }

}
