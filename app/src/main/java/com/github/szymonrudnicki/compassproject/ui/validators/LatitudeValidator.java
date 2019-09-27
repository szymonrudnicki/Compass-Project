package com.github.szymonrudnicki.compassproject.ui.validators;

public class LatitudeValidator {
    public static boolean isValid(String text) {
        double MIN_LATITUDE = -90;
        double MAX_LATITUDE = 90;

        try {
            double doubleValue = Double.parseDouble(text);
            return doubleValue < MAX_LATITUDE && doubleValue > MIN_LATITUDE;
        } catch (Exception e) {
            return false;
        }
    }
}
