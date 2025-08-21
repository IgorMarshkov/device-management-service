package com.interview.mapper;

import java.time.LocalDateTime;

import com.interview.dto.DeviceCreateRequestDto;
import com.interview.dto.DeviceResponseDto;
import com.interview.dto.DeviceUpdateRequestDto;
import com.interview.entity.DeviceEntity;
import com.interview.enums.DeviceState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class DeviceMapperTest {

    @Autowired
    private DeviceMapper deviceMapper;

    @Test
    void testToResponseDto() {
        // given
        DeviceEntity device = new DeviceEntity();
        device.setId(1L);
        device.setName("Pixel 9");
        device.setBrand("Google");
        device.setState(DeviceState.AVAILABLE);
        device.setCreationTime(LocalDateTime.now());

        // when
        DeviceResponseDto result = deviceMapper.toResponseDto(device);

        // then
        assertNotNull(result);
        assertEquals(device.getId(), result.getId());
        assertEquals(device.getName(), result.getName());
        assertEquals(device.getBrand(), result.getBrand());
        assertEquals(device.getState(), result.getState());
        assertEquals(device.getCreationTime(), result.getCreationTime());
    }

    @Test
    void testToEntity() {
        // given
        DeviceCreateRequestDto createDto = new DeviceCreateRequestDto();
        createDto.setName("Pixel 9");
        createDto.setBrand("Google");

        // when
        DeviceEntity result = deviceMapper.toEntity(createDto);

        // then
        assertNotNull(result);
        assertEquals(createDto.getName(), result.getName());
        assertEquals(createDto.getBrand(), result.getBrand());
    }

    @Test
    void testUpdateEntity() {
        // given
        DeviceEntity device = new DeviceEntity();
        device.setId(1L);
        device.setName("Pixel 9");
        device.setBrand("Google");
        device.setState(DeviceState.AVAILABLE);
        device.setCreationTime(LocalDateTime.now());

        DeviceUpdateRequestDto updateDto = new DeviceUpdateRequestDto();
        updateDto.setName("New Name");
        updateDto.setBrand(null);

        // when
        deviceMapper.updateEntity(device, updateDto);

        // then
        assertEquals(updateDto.getName(), device.getName());
        assertEquals("Google", device.getBrand());
    }

}
