package com.interview.exception;

/**
 * Exception to be thrown when a device validation fails.
 */
public class DeviceValidationException extends RuntimeException {

    public DeviceValidationException(String message) {
        super(message);
    }

}
