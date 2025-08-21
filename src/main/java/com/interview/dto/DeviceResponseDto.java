package com.interview.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.interview.enums.DeviceState;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Device model with related information")
public class DeviceResponseDto {

    @Schema(description = "Device ID")
    private Long id;
    @Schema(description = "Device name")
    private String name;
    @Schema(description = "Device brand name")
    private String brand;
    @Schema(description = "Actual device state")
    private DeviceState state;

    @Schema(description = "Device creation time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime creationTime;

    public DeviceResponseDto() {
    }

    public DeviceResponseDto(Long id, String name, String brand, DeviceState state, LocalDateTime creationTime) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.state = state;
        this.creationTime = creationTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public DeviceState getState() {
        return state;
    }

    public void setState(DeviceState state) {
        this.state = state;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

}