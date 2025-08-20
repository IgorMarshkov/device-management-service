package com.interview.exception;

/**
 * Exception to be thrown when a device is not found.
 */
public class DeviceNotFoundException extends RuntimeException {

    public DeviceNotFoundException(String message) {
        super(message);
    }

    public DeviceNotFoundException(Long id) {
        super("Device not found with id: " + id);
    }

}
