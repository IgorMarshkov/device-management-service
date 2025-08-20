package com.interview.contoller;

import java.time.LocalDateTime;

import com.interview.controller.DeviceController;
import com.interview.dto.DeviceCreateRequestDto;
import com.interview.dto.DeviceResponseDto;
import com.interview.enums.DeviceState;
import com.interview.exception.DeviceNotFoundException;
import com.interview.service.DeviceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeviceController.class)
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeviceService deviceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createDevice_ValidInput_ReturnsCreated() throws Exception {
        DeviceCreateRequestDto createDto = new DeviceCreateRequestDto("iPhone 15", "Apple");
        DeviceResponseDto responseDto = new DeviceResponseDto(1L, "iPhone 15", "Apple",
                DeviceState.AVAILABLE, LocalDateTime.now());

        when(deviceService.createDevice(any(DeviceCreateRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("iPhone 15"))
                .andExpect(jsonPath("$.brand").value("Apple"))
                .andExpect(jsonPath("$.state").value("AVAILABLE"));
    }

    @Test
    void createDevice_InvalidInput_ReturnsBadRequest() throws Exception {
        DeviceCreateRequestDto createDto = new DeviceCreateRequestDto("", "");

        mockMvc.perform(post("/api/v1/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDevice_ExistingId_ReturnsDevice() throws Exception {
        DeviceResponseDto responseDto = new DeviceResponseDto(1L, "iPhone 15", "Apple",
                DeviceState.AVAILABLE, LocalDateTime.now());

        when(deviceService.getDeviceById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/devices/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("iPhone 15"));
    }

    @Test
    void getDevice_NonExistingId_ReturnsNotFound() throws Exception {
        when(deviceService.getDeviceById(999L)).thenThrow(new DeviceNotFoundException(999L));

        mockMvc.perform(get("/api/v1/devices/999"))
                .andExpect(status().isNotFound());
    }

}
