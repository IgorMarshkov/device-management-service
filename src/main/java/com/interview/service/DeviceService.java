package com.interview.service;

import com.interview.dto.DeviceCreateRequestDto;
import com.interview.dto.DeviceResponseDto;
import com.interview.dto.DeviceUpdateRequestDto;
import com.interview.enums.DeviceState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    /**
     * Delete a device by id. The device must not be in use.
     * @param id - Device ID.
     */
    void deleteDevice(Long id);

    /**
     * Update a device by id. The device must not be in use.
     * Only parameters that are not null will be updated.
     * @param id - Device ID.
     * @param updateDto - DTO object with the device information to update.
     * @return - DTO object with the updated device information.
     */
    DeviceResponseDto updateDevice(Long id, DeviceUpdateRequestDto updateDto);

    /**
     * Get devices by brand and/or state.
     * @param brand - Brand name. Can be null.
     * @param state - Device state. Can be null.
     * @param pageable - Pageable object.
     * @return - Page of device DTO objects.
     */
    Page<DeviceResponseDto> getDevices(String brand, DeviceState state, Pageable pageable);

}
