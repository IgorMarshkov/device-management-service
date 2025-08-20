package com.interview.service;

import com.interview.dto.DeviceCreateRequestDto;
import com.interview.dto.DeviceResponseDto;

/**
 * Device service interface to handle business logic with {@link com.interview.entity.DeviceEntity}.
 */
public interface DeviceService {

    /**
     * Create a new device.
     * @param createDto - DTO object to create a new device.
     * @return - DTO object with the created device information.
     */
    DeviceResponseDto createDevice(DeviceCreateRequestDto createDto);

    /**
     * Get device by id.
     * @param id - Device ID.
     * @return - DTO object with the device information.
     */
    DeviceResponseDto getDeviceById(Long id);

}
