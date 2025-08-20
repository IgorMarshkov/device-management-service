package com.interview.service;

import java.time.LocalDateTime;

import com.interview.dto.DeviceCreateRequestDto;
import com.interview.dto.DeviceResponseDto;
import com.interview.entity.DeviceEntity;
import com.interview.enums.DeviceState;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

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
        String deviceName = "iPhone 15";
        String brand = "Apple";

        DeviceCreateRequestDto createDto = new DeviceCreateRequestDto(deviceName, brand);
        DeviceEntity deviceEntity = new DeviceEntity(deviceName, brand);
        DeviceEntity savedDevice = new DeviceEntity(deviceName, brand, DeviceState.AVAILABLE);
        savedDevice.setId(1L);
        savedDevice.setCreationTime(LocalDateTime.now());

        DeviceResponseDto expectedResponse = new DeviceResponseDto(1L, deviceName, brand,
                DeviceState.AVAILABLE, LocalDateTime.now());

        when(deviceMapper.toEntity(createDto)).thenReturn(deviceEntity);
        when(deviceRepository.save(deviceEntity)).thenReturn(savedDevice);
        when(deviceMapper.toResponseDto(savedDevice)).thenReturn(expectedResponse);

        // when
        DeviceResponseDto result = deviceService.createDevice(createDto);

        // then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("iPhone 15", result.getName());

        verify(deviceRepository).save(deviceEntity);
    }

}
