package com.github.szymonrudnicki.compassproject.ui.validators;

import com.rengwuxian.materialedittext.validation.METValidator;

import androidx.annotation.NonNull;

public class LongitudeValidator extends METValidator {

  public LongitudeValidator(@NonNull String errorMessage) {
    super(errorMessage);
  }

  @Override
  public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
    double MIN_LONGITUDE = -180;
    double MAX_LONGITUDE = 180;

    try {
      double doubleValue = Double.parseDouble(text.toString());
      return doubleValue < MAX_LONGITUDE && doubleValue > MIN_LONGITUDE;
    } catch (Exception e) {
      return false;
    }
  }
}
