package com.interview.service.impl;

import com.interview.dto.DeviceCreateRequestDto;
import com.interview.dto.DeviceResponseDto;
import com.interview.dto.DeviceUpdateRequestDto;
import com.interview.entity.DeviceEntity;
import com.interview.enums.DeviceState;
import com.interview.exception.DeviceNotFoundException;
import com.interview.exception.DeviceValidationException;
import com.interview.mapper.DeviceMapper;
import com.interview.repository.DeviceRepository;
import com.interview.service.DeviceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Device service implementation to handle business logic with {@link com.interview.entity.DeviceEntity}.
 */
@Service
@Transactional(readOnly = true)
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceMapper deviceMapper;

    public DeviceServiceImpl(DeviceRepository deviceRepository, DeviceMapper deviceMapper) {
        this.deviceRepository = deviceRepository;
        this.deviceMapper = deviceMapper;
    }

    @Override
    @Transactional
    public DeviceResponseDto createDevice(DeviceCreateRequestDto createDto) {
        DeviceEntity newEntity = deviceMapper.toEntity(createDto);
        newEntity.setState(DeviceState.AVAILABLE);
        DeviceEntity savedDevice = deviceRepository.save(newEntity);
        return deviceMapper.toResponseDto(savedDevice);
    }

    @Override
    public DeviceResponseDto getDeviceById(Long id) {
        DeviceEntity entity = deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));
        return deviceMapper.toResponseDto(entity);
    }

    @Override
    @Transactional
    public void deleteDevice(Long id) {
        DeviceEntity entity = deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));

        if (entity.getState() == DeviceState.IN_USE) {
            throw new DeviceValidationException("Cannot delete device that is currently in use");
        }

        deviceRepository.deleteById(id);
    }

    @Override
    @Transactional
    public DeviceResponseDto updateDevice(Long id, DeviceUpdateRequestDto updateDto) {
        DeviceEntity deviceEntity = deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));

        validateUpdate(deviceEntity, updateDto);
        deviceMapper.updateEntity(deviceEntity, updateDto);

        DeviceEntity updatedDeviceEntity = deviceRepository.save(deviceEntity);
        return deviceMapper.toResponseDto(updatedDeviceEntity);
    }

    @Override
    public Page<DeviceResponseDto> getDevices(String brand, DeviceState state, Pageable pageable) {
        Page<DeviceEntity> devices;

        if (brand != null && state != null) {
            devices = deviceRepository.findByBrandIgnoreCaseAndState(brand, state, pageable);
        } else if (brand != null) {
            devices = deviceRepository.findByBrandIgnoreCase(brand, pageable);
        } else if (state != null) {
            devices = deviceRepository.findByState(state, pageable);
        } else {
            devices = deviceRepository.findAll(pageable);
        }

        return devices.map(deviceMapper::toResponseDto);
    }

    private void validateUpdate(DeviceEntity deviceEntity, DeviceUpdateRequestDto updateDto) {
        if (deviceEntity.getState() == DeviceState.IN_USE) {
            if (updateDto.getName() != null && !updateDto.getName().equals(deviceEntity.getName())) {
                throw new DeviceValidationException("Cannot update name of device that is in use");
            }
            if (updateDto.getBrand() != null && !updateDto.getBrand().equals(deviceEntity.getBrand())) {
                throw new DeviceValidationException("Cannot update brand of device that is in use");
            }
        }
    }

}
