package com.interview.service.impl;

import com.interview.dto.DeviceCreateRequestDto;
import com.interview.dto.DeviceResponseDto;
import com.interview.entity.DeviceEntity;
import com.interview.enums.DeviceState;
import com.interview.exception.DeviceNotFoundException;
import com.interview.mapper.DeviceMapper;
import com.interview.repository.DeviceRepository;
import com.interview.service.DeviceService;
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

}
