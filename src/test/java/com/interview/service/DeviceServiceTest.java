package com.interview.service;

import java.time.LocalDateTime;
import java.util.Optional;

import com.interview.dto.DeviceCreateRequestDto;
import com.interview.dto.DeviceResponseDto;
import com.interview.dto.DeviceUpdateRequestDto;
import com.interview.entity.DeviceEntity;
import com.interview.enums.DeviceState;
import com.interview.exception.DeviceNotFoundException;
import com.interview.exception.DeviceValidationException;
import com.interview.mapper.DeviceMapper;
import com.interview.mapper.DeviceMapperImpl;
import com.interview.repository.DeviceRepository;
import com.interview.service.impl.DeviceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    private static final String DEVICE_NAME = "iPhone 15";
    private static final String BRAND = "Apple";

    @Mock
    private DeviceRepository deviceRepository;

    @Spy
    private DeviceMapper deviceMapper = new DeviceMapperImpl();

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

    @Test
    void deleteDevice_InUseDevice_ThrowsException() {
        // given
        DeviceEntity device = new DeviceEntity(DEVICE_NAME, BRAND, DeviceState.IN_USE);
        device.setId(1L);

        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));

        // when & then
        assertThrows(DeviceValidationException.class, () -> deviceService.deleteDevice(1L));
        verify(deviceRepository, never()).deleteById(any());
    }

    @Test
    void deleteDevice_AvailableDevice_DeletesSuccessfully() {
        // given
        DeviceEntity device = new DeviceEntity(DEVICE_NAME, BRAND, DeviceState.AVAILABLE);
        device.setId(1L);

        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));

        // when & then
        assertDoesNotThrow(() -> deviceService.deleteDevice(1L));
        verify(deviceRepository).deleteById(1L);
    }

    @Test
    void updateDevice_InUseDeviceNameUpdate_ThrowsException() {
        // given
        DeviceEntity device = new DeviceEntity(DEVICE_NAME, BRAND, DeviceState.IN_USE);
        device.setId(1L);
        DeviceUpdateRequestDto updateDto = new DeviceUpdateRequestDto("iPhone 15 Pro", null, null);

        // when
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));

        // then
        assertThrows(DeviceValidationException.class, () -> deviceService.updateDevice(1L, updateDto));
    }

    @Test
    void updateDevice_DeviceNameBrandUpdate_UpdatesSuccessfully() {
        // given
        LocalDateTime creationTime = LocalDateTime.now();
        DeviceEntity existingDevice = new DeviceEntity(DEVICE_NAME, BRAND, DeviceState.AVAILABLE);
        existingDevice.setId(1L);
        existingDevice.setCreationTime(creationTime);

        String updatedDeviceName = "iPhone 15 Pro";
        String updatedBrand = "New Apple";
        DeviceUpdateRequestDto updateDto = new DeviceUpdateRequestDto(updatedDeviceName, updatedBrand, DeviceState.IN_USE);

        DeviceEntity expectedDeviceEntry = new DeviceEntity(updatedDeviceName, updatedBrand,
                DeviceState.IN_USE);
        expectedDeviceEntry.setId(1L);
        expectedDeviceEntry.setCreationTime(creationTime);

        when(deviceRepository.findById(1L)).thenReturn(Optional.of(existingDevice));

        // when
        deviceService.updateDevice(1L, updateDto);

        // then
        verify(deviceRepository).save(expectedDeviceEntry);
    }

    @Test
    void updateDevice_SkippedBrandUpdate_UpdatesSuccessfully() {
        // given
        LocalDateTime creationTime = LocalDateTime.now();
        DeviceEntity existingDevice = new DeviceEntity(DEVICE_NAME, BRAND, DeviceState.AVAILABLE);
        existingDevice.setId(1L);
        existingDevice.setCreationTime(creationTime);

        String updatedDeviceName = "iPhone 15 Pro";
        DeviceUpdateRequestDto updateDto = new DeviceUpdateRequestDto(updatedDeviceName, null, DeviceState.IN_USE);

        DeviceEntity expectedDeviceEntry = new DeviceEntity(updatedDeviceName, BRAND,
                DeviceState.IN_USE);
        expectedDeviceEntry.setId(1L);
        expectedDeviceEntry.setCreationTime(creationTime);

        when(deviceRepository.findById(1L)).thenReturn(Optional.of(existingDevice));

        // when
        deviceService.updateDevice(1L, updateDto);

        // then
        verify(deviceRepository).save(expectedDeviceEntry);
    }

}
