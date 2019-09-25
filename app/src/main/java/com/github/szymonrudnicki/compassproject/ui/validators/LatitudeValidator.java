package com.github.szymonrudnicki.compassproject.ui.validators;

import com.rengwuxian.materialedittext.validation.METValidator;

import androidx.annotation.NonNull;

public class LatitudeValidator extends METValidator {

  public LatitudeValidator(@NonNull String errorMessage) {
    super(errorMessage);
  }

  @Override
  public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
    double MIN_LATITUDE = -90;
    double MAX_LATITUDE = 90;

    try {
      double doubleValue = Double.parseDouble(text.toString());
      return doubleValue < MAX_LATITUDE && doubleValue > MIN_LATITUDE;
    } catch (Exception e) {
      return false;
    }
  }
}
