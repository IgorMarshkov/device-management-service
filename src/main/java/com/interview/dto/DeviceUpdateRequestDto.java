package com.interview.dto;

import com.interview.enums.DeviceState;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Model to be able update a specific device fully or partially")
public class DeviceUpdateRequestDto {

    @Schema(description = "Device name", maxLength = 255, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String name;
    @Schema(name = "Device brand name", maxLength = 255, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String brand;
    @Schema(name = "New device state", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private DeviceState state;

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

}
