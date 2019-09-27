package com.github.szymonrudnicki.compassproject.utils;

import android.hardware.SensorEvent;
import android.hardware.SensorManager;

public class SensorUtils {
  public static double calculateAzimuth(SensorEvent event) {
    float[] rotationMatrix = new float[9];
    float[] orientation = new float[3];

    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
    SensorManager.getOrientation(rotationMatrix, orientation);

    float azimuthInRadians = orientation[0];
    double azimuthInDegrees = Math.toDegrees(azimuthInRadians);
    return (azimuthInDegrees + 360) % 360;
  }
}
