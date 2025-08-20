package com.interview.mapper;

import com.interview.dto.DeviceCreateRequestDto;
import com.interview.dto.DeviceResponseDto;
import com.interview.entity.DeviceEntity;
import org.mapstruct.Mapper;

/**
 * Device mapper interface to map {@link com.interview.entity.DeviceEntity} objects into DTO objects.
 */
@Mapper(componentModel = "spring")
public interface DeviceMapper {

    DeviceResponseDto toResponseDto(DeviceEntity entity);

    DeviceEntity toEntity(DeviceCreateRequestDto createDto);

}
