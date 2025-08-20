package com.interview.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Model to create a new device")
public class DeviceCreateRequestDto {

    @NotBlank(message = "Device name is required")
    @Schema(description = "Device name", maxLength = 255, requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "Device brand name is required")
    @Schema(description = "Device brand name", maxLength = 255, requiredMode = Schema.RequiredMode.REQUIRED)
    private String brand;

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

}
