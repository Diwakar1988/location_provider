package com.github.diwakar1988.location.core;


public class LocationException extends Exception {
    public LocationException() {
        super("(Couldn't get the location. Make sure location is enabled on the device and all location permissions are allowed.)");
    }

    public LocationException(String message) {
        super(message);
    }
}
