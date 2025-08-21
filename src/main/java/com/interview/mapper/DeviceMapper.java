package com.interview.mapper;

import com.interview.dto.DeviceCreateRequestDto;
import com.interview.dto.DeviceResponseDto;
import com.interview.dto.DeviceUpdateRequestDto;
import com.interview.entity.DeviceEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Device mapper interface to map {@link com.interview.entity.DeviceEntity} objects into DTO objects.
 */
@Mapper(componentModel = "spring")
public interface DeviceMapper {

    DeviceResponseDto toResponseDto(DeviceEntity entity);

    DeviceEntity toEntity(DeviceCreateRequestDto createDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget DeviceEntity entity, DeviceUpdateRequestDto updateDto);

}
