package com.gogidix.rapidassist.geo.location.service.domain.model;

public record Coordinates(
    double latitude,
    double longitude,
    Double altitude,
    Double accuracy,
    Double altitudeAccuracy,
    Double heading,
    Double speed
) {

    public static Coordinates of(double latitude, double longitude) {
        return new Coordinates(latitude, longitude, null, null, null, null, null);
    }

    public static Coordinates of(double latitude, double longitude, double accuracy) {
        return new Coordinates(latitude, longitude, null, accuracy, null, null, null);
    }

    public Coordinates withAltitude(Double altitude) {
        return new Coordinates(latitude, longitude, altitude, accuracy, altitudeAccuracy, heading, speed);
    }

    public Coordinates withAccuracy(Double accuracy) {
        return new Coordinates(latitude, longitude, altitude, accuracy, altitudeAccuracy, heading, speed);
    }

    public boolean isValid() {
        return latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180;
    }

    public String toWKT() {
        return String.format("POINT (%f %f)", longitude, latitude);
    }

    public String toDecimalDegrees() {
        return String.format("%.6f, %.6f", latitude, longitude);
    }
}