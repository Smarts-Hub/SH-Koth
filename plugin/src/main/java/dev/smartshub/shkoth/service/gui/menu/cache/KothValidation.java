package dev.smartshub.shkoth.service.gui.menu.cache;

import java.util.ArrayList;
import java.util.List;

public class KothValidation {

    public static ValidationResult validate(KothTempData tempData) {
        List<String> errors = new ArrayList<>();

        if (tempData.getId() == null || tempData.getId().trim().isEmpty()) {
            errors.add("ID cannot be empty");
        } else if (!tempData.getId().matches("^[A-Za-z0-9_-]+$")) {
            errors.add("ID may only contain letters, digits, '_' and '-'");
        }

        if (tempData.getDisplayName() == null || tempData.getDisplayName().trim().isEmpty()) {
            errors.add("Display name cannot be empty");
        }

        if (tempData.getMaxTime() <= 0) {
            errors.add("Max time must be greater than 0");
        }

        if (tempData.getCaptureTime() <= 0) {
            errors.add("Capture time must be greater than 0");
        }

        if (tempData.getCaptureTime() > tempData.getMaxTime()) {
            errors.add("Capture time must not exceed max time");
        }

        if (tempData.getArea() == null) {
            errors.add("Area must be set");
        }

        if (tempData.getType() == null) {
            errors.add("KOTH type must be set");
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    public record ValidationResult(boolean valid, List<String> errors) {}
}
