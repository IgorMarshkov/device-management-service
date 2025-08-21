package com.interview.service;

import java.time.LocalDateTime;
import java.util.List;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @Test
    void getDevices_WithPagination_ReturnsPagedResult() {
        Pageable pageable = Pageable.ofSize(20);
        List<DeviceEntity> devices = List.of(
                new DeviceEntity("iPhone 15", "Apple", DeviceState.AVAILABLE),
                new DeviceEntity("Galaxy S24", "Samsung", DeviceState.IN_USE)
        );
        Page<DeviceEntity> devicePage = new PageImpl<>(devices, pageable, 2);

        List<DeviceResponseDto> expectedDtos = List.of(
                new DeviceResponseDto(1L, "iPhone 15", "Apple", DeviceState.AVAILABLE, LocalDateTime.now()),
                new DeviceResponseDto(2L, "Galaxy S24", "Samsung", DeviceState.IN_USE, LocalDateTime.now())
        );

        when(deviceRepository.findAll(pageable)).thenReturn(devicePage);
        when(deviceMapper.toResponseDto(devices.get(0))).thenReturn(expectedDtos.get(0));
        when(deviceMapper.toResponseDto(devices.get(1))).thenReturn(expectedDtos.get(1));

        Page<DeviceResponseDto> result = deviceService.getDevices(null, null, pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    void getDevices_WithBrandFilter_ReturnsFilteredResults() {
        Pageable pageable = PageRequest.of(0, 20);
        List<DeviceEntity> appleDevices = List.of(
                new DeviceEntity("iPhone 15", BRAND, DeviceState.AVAILABLE),
                new DeviceEntity("iPhone 16", BRAND, DeviceState.IN_USE)
        );
        Page<DeviceEntity> devicePage = new PageImpl<>(appleDevices, pageable, 2);

        List<DeviceResponseDto> expectedDtos = List.of(
                new DeviceResponseDto(1L, "iPhone 15", BRAND, DeviceState.AVAILABLE, LocalDateTime.now()),
                new DeviceResponseDto(3L, "iPhone 16", BRAND, DeviceState.IN_USE, LocalDateTime.now())
        );

        when(deviceRepository.findByBrandIgnoreCase(BRAND, pageable)).thenReturn(devicePage);
        when(deviceMapper.toResponseDto(appleDevices.get(0))).thenReturn(expectedDtos.get(0));
        when(deviceMapper.toResponseDto(appleDevices.get(1))).thenReturn(expectedDtos.get(1));

        Page<DeviceResponseDto> result = deviceService.getDevices(BRAND, null, pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().stream().allMatch(d -> d.getBrand().equals(BRAND)));
    }

    @Test
    void getDevices_WithStateFilter_ReturnsFilteredResults() {
        Pageable pageable = PageRequest.of(0, 20);
        List<DeviceEntity> availableDevices = List.of(
                new DeviceEntity("iPhone 15", "Apple", DeviceState.AVAILABLE),
                new DeviceEntity("Galaxy S24", "Samsung", DeviceState.AVAILABLE)
        );
        Page<DeviceEntity> devicePage = new PageImpl<>(availableDevices, pageable, 2);

        List<DeviceResponseDto> expectedDtos = List.of(
                new DeviceResponseDto(1L, "iPhone 15", "Apple", DeviceState.AVAILABLE, LocalDateTime.now()),
                new DeviceResponseDto(2L, "Galaxy S24", "Samsung", DeviceState.AVAILABLE, LocalDateTime.now())
        );

        when(deviceRepository.findByState(DeviceState.AVAILABLE, pageable)).thenReturn(devicePage);
        when(deviceMapper.toResponseDto(availableDevices.get(0))).thenReturn(expectedDtos.get(0));
        when(deviceMapper.toResponseDto(availableDevices.get(1))).thenReturn(expectedDtos.get(1));

        Page<DeviceResponseDto> result = deviceService.getDevices(null, DeviceState.AVAILABLE, pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().stream().allMatch(d -> d.getState() == DeviceState.AVAILABLE));
    }

    @Test
    void getDevices_WithBrandAndStateFilter_ReturnsFilteredResults() {
        Pageable pageable = PageRequest.of(0, 20);
        DeviceState state = DeviceState.AVAILABLE;
        List<DeviceEntity> filteredDevices = List.of(
                new DeviceEntity(DEVICE_NAME, BRAND, DeviceState.AVAILABLE)
        );
        Page<DeviceEntity> devicePage = new PageImpl<>(filteredDevices, pageable, 1);

        DeviceResponseDto expectedDto = new DeviceResponseDto(1L, DEVICE_NAME, BRAND,
                DeviceState.AVAILABLE, LocalDateTime.now());

        when(deviceRepository.findByBrandIgnoreCaseAndState(BRAND, state, pageable)).thenReturn(devicePage);
        when(deviceMapper.toResponseDto(filteredDevices.getFirst())).thenReturn(expectedDto);

        Page<DeviceResponseDto> result = deviceService.getDevices(BRAND, state, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(BRAND, result.getContent().getFirst().getBrand());
        assertEquals(DeviceState.AVAILABLE, result.getContent().getFirst().getState());
    }

}
