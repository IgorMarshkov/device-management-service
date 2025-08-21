package com.interview.dto;

import java.util.Objects;

import com.interview.enums.DeviceState;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Model to be able update a specific device fully or partially")
public class DeviceUpdateRequestDto {

    @Schema(description = "Device name", maxLength = 255, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String name;
    @Schema(description = "Device brand name", maxLength = 255, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String brand;
    @Schema(description = "New device state", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private DeviceState state;

    public DeviceUpdateRequestDto() {
    }

    public DeviceUpdateRequestDto(String name, String brand, DeviceState state) {
        this.name = name;
        this.brand = brand;
        this.state = state;
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DeviceUpdateRequestDto that)) {
            return false;
        }
        return Objects.equals(getName(), that.getName()) && Objects.equals(getBrand(),
                that.getBrand()) && getState() == that.getState();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getBrand(), getState());
    }

}
