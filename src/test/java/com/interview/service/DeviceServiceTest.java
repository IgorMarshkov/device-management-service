package com.interview.service;

import java.time.LocalDateTime;
import java.util.Optional;

import com.interview.dto.DeviceCreateRequestDto;
import com.interview.dto.DeviceResponseDto;
import com.interview.entity.DeviceEntity;
import com.interview.enums.DeviceState;
import com.interview.exception.DeviceNotFoundException;
import com.interview.mapper.DeviceMapper;
import com.interview.repository.DeviceRepository;
import com.interview.service.impl.DeviceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    private static final String DEVICE_NAME = "iPhone 15";
    private static final String BRAND = "Apple";

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private DeviceMapper deviceMapper;

    private DeviceService deviceService;

    @BeforeEach
    void setUp() {
        deviceService = new DeviceServiceImpl(deviceRepository, deviceMapper);
    }

    @Test
    void createDevice_ValidInput_ReturnsDeviceResponse() {
        // given
        DeviceCreateRequestDto createDto = new DeviceCreateRequestDto(DEVICE_NAME, BRAND);
        DeviceEntity deviceEntity = new DeviceEntity(DEVICE_NAME, BRAND);
        DeviceEntity savedDevice = new DeviceEntity(DEVICE_NAME, BRAND, DeviceState.AVAILABLE);
        savedDevice.setId(1L);
        savedDevice.setCreationTime(LocalDateTime.now());

        DeviceResponseDto expectedResponse = new DeviceResponseDto(1L, DEVICE_NAME, BRAND,
                DeviceState.AVAILABLE, LocalDateTime.now());

        when(deviceMapper.toEntity(createDto)).thenReturn(deviceEntity);
        when(deviceRepository.save(deviceEntity)).thenReturn(savedDevice);
        when(deviceMapper.toResponseDto(savedDevice)).thenReturn(expectedResponse);

        // when
        DeviceResponseDto result = deviceService.createDevice(createDto);

        // then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(DEVICE_NAME, result.getName());

        verify(deviceRepository).save(deviceEntity);
    }

    @Test
    void getDeviceById_ExistingId_ReturnsDevice() {
        // given
        DeviceEntity device = new DeviceEntity(DEVICE_NAME, BRAND, DeviceState.AVAILABLE);
        device.setId(1L);
        DeviceResponseDto expectedResponse = new DeviceResponseDto(1L, DEVICE_NAME, BRAND,
                DeviceState.AVAILABLE, LocalDateTime.now());

        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));
        when(deviceMapper.toResponseDto(device)).thenReturn(expectedResponse);

        // when
        DeviceResponseDto result = deviceService.getDeviceById(1L);

        // then
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getDeviceById_NonExistingId_ThrowsException() {
        // given
        when(deviceRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(DeviceNotFoundException.class, () -> deviceService.getDeviceById(999L));
    }

}
