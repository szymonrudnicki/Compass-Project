package com.github.szymonrudnicki.compassproject.ui.validators;

public class LongitudeValidator {
    public static boolean isValid(String text) {
        double MIN_LONGITUDE = -180;
        double MAX_LONGITUDE = 180;

        try {
            double doubleValue = Double.parseDouble(text);
            return doubleValue < MAX_LONGITUDE && doubleValue > MIN_LONGITUDE;
        } catch (Exception e) {
            return false;
        }
    }
}
