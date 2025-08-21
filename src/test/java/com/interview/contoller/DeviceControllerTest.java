package com.interview.contoller;

import java.time.LocalDateTime;
import java.util.List;

import com.interview.controller.DeviceController;
import com.interview.dto.DeviceCreateRequestDto;
import com.interview.dto.DeviceResponseDto;
import com.interview.dto.DeviceUpdateRequestDto;
import com.interview.enums.DeviceState;
import com.interview.exception.DeviceNotFoundException;
import com.interview.exception.DeviceValidationException;
import com.interview.service.DeviceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @Test
    void deleteDevice_InactiveDevice_ReturnsSuccess() throws Exception {
        mockMvc.perform(delete("/api/v1/devices/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteDevice_InUseDevice_ReturnsBadRequest() throws Exception {
        doThrow(new DeviceValidationException("Cannot delete device that is currently in use"))
                .when(deviceService).deleteDevice(1L);

        mockMvc.perform(delete("/api/v1/devices/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateDevice_InUseDevice_ReturnsBadRequest() throws Exception {
        DeviceUpdateRequestDto updateDto = new DeviceUpdateRequestDto("Pixel", null, DeviceState.AVAILABLE);

        doThrow(new DeviceValidationException("Cannot update name of device that is in use"))
                .when(deviceService).updateDevice(1L, updateDto);

        mockMvc.perform(put("/api/v1/devices/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateDevice_ValidInput_ReturnsUpdatedDevice() throws Exception {
        DeviceResponseDto responseDto = new DeviceResponseDto(1L, "iPhone 15", "Apple", DeviceState.AVAILABLE, LocalDateTime.now());
        DeviceUpdateRequestDto updateDto = new DeviceUpdateRequestDto("iPhone 15", "Apple", DeviceState.AVAILABLE);

        when(deviceService.updateDevice(1L, updateDto)).thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/devices/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("iPhone 15"))
                .andExpect(jsonPath("$.brand").value("Apple"))
                .andExpect(jsonPath("$.state").value("AVAILABLE"));
    }

    @Test
    void getDevices_WithPagination_ReturnsPagedResponse() throws Exception {
        List<DeviceResponseDto> devices = List.of(
                new DeviceResponseDto(1L, "iPhone 15", "Apple", DeviceState.AVAILABLE, LocalDateTime.now()),
                new DeviceResponseDto(2L, "Galaxy S24", "Samsung", DeviceState.IN_USE, LocalDateTime.now())
        );

        Page<DeviceResponseDto> pagedDevices = new PageImpl<>(devices, PageRequest.of(0, 20), 2);

        when(deviceService.getDevices(eq(null), eq(null), any(Pageable.class))).thenReturn(pagedDevices);

        mockMvc.perform(get("/api/v1/devices")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    void getDevices_WithBrandFilter_ReturnsFilteredResults() throws Exception {
        List<DeviceResponseDto> appleDevices = List.of(
                new DeviceResponseDto(1L, "iPhone 15", "Apple", DeviceState.AVAILABLE, LocalDateTime.now()),
                new DeviceResponseDto(3L, "iPhone 16", "Apple", DeviceState.IN_USE, LocalDateTime.now())
        );

        Page<DeviceResponseDto> pagedDevices = new PageImpl<>(appleDevices, PageRequest.of(0, 20), 2);

        when(deviceService.getDevices(eq("Apple"), eq(null), any(Pageable.class))).thenReturn(pagedDevices);

        mockMvc.perform(get("/api/v1/devices")
                        .param("brand", "Apple")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].brand").value("Apple"))
                .andExpect(jsonPath("$.content[1].brand").value("Apple"));
    }

    @Test
    void getDevices_WithStateFilter_ReturnsFilteredResults() throws Exception {
        List<DeviceResponseDto> availableDevices = List.of(
                new DeviceResponseDto(1L, "iPhone 15", "Apple", DeviceState.AVAILABLE, LocalDateTime.now())
        );

        Page<DeviceResponseDto> pagedDevices = new PageImpl<>(availableDevices, PageRequest.of(0, 20), 1);

        when(deviceService.getDevices(eq(null), eq(DeviceState.AVAILABLE), any(Pageable.class))).thenReturn(pagedDevices);

        mockMvc.perform(get("/api/v1/devices")
                        .param("state", "AVAILABLE")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].state").value("AVAILABLE"));
    }

    @Test
    void getDevices_WithSorting_ReturnsSortedResults() throws Exception {
        List<DeviceResponseDto> devices = List.of(
                new DeviceResponseDto(1L, "iPhone 15", "Apple", DeviceState.AVAILABLE, LocalDateTime.now())
        );

        Page<DeviceResponseDto> pagedDevices = new PageImpl<>(devices, PageRequest.of(0, 20), 1);

        when(deviceService.getDevices(eq(null), eq(null), any(Pageable.class))).thenReturn(pagedDevices);

        mockMvc.perform(get("/api/v1/devices")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sortBy", "name")
                        .param("sortDirection", "DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void getDevices_InvalidSortDirection_UsesDefaultAscending() throws Exception {
        List<DeviceResponseDto> devices = List.of(
                new DeviceResponseDto(1L, "iPhone 15", "Apple", DeviceState.AVAILABLE, LocalDateTime.now())
        );

        Page<DeviceResponseDto> pagedDevices = new PageImpl<>(devices, PageRequest.of(0, 20), 1);

        when(deviceService.getDevices(eq(null), eq(null), any(Pageable.class))).thenReturn(pagedDevices);

        mockMvc.perform(get("/api/v1/devices")
                        .param("sortDirection", "INVALID"))
                .andExpect(status().isOk());
    }

}
