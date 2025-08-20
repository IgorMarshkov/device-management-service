package com.interview.controller;

import java.util.List;

import com.interview.dto.DeviceCreateRequestDto;
import com.interview.dto.DeviceResponseDto;
import com.interview.dto.DeviceUpdateRequestDto;
import com.interview.dto.ErrorResponseDto;
import com.interview.enums.DeviceState;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/devices",
        produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Device Management", description = "APIs for managing device resources")
public class DeviceController {

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new device", description = "Creates a new device resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Device created successfully",
                    content = {@Content(schema = @Schema(implementation = DeviceResponseDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))})
    })
    public ResponseEntity<DeviceResponseDto> createDevice(@Valid @RequestBody DeviceCreateRequestDto createDto) {
        // TODO: Implement
        return new ResponseEntity<>(new DeviceResponseDto(), HttpStatus.CREATED);
    }

    @PutMapping(path ="/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update device", description = "Fully or partially updates an existing device")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device updated successfully",
                    content = {@Content(schema = @Schema(implementation = DeviceResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Device not found",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid update data",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))})
    })
    public ResponseEntity<DeviceResponseDto> updateDevice(
            @Parameter(description = "Device ID") @PathVariable Long id,
            @RequestBody DeviceUpdateRequestDto updateDto) {
        // TODO: Implement
        return ResponseEntity.ok(new DeviceResponseDto());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get device by ID", description = "Retrieves a single device by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device found",
                    content = {@Content(schema = @Schema(implementation = DeviceResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Device not found",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))})
    })
    public ResponseEntity<DeviceResponseDto> getDevice(
            @Parameter(description = "Device ID") @PathVariable Long id) {
        // TODO: Implement
        return ResponseEntity.ok(new DeviceResponseDto());
    }

    @GetMapping
    @Operation(summary = "Get all devices", description = "Retrieves all devices with optional filtering")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Devices retrieved successfully",
                    content = {@Content(array = @ArraySchema(schema = @Schema(implementation = DeviceResponseDto.class)))})
    })
    public ResponseEntity<List<DeviceResponseDto>> getAllDevices(
            @Parameter(description = "Filter by brand") @RequestParam(required = false) String brand,
            @Parameter(description = "Filter by state") @RequestParam(required = false) DeviceState state) {
        // TODO: Implement
        return ResponseEntity.ok(List.of(new DeviceResponseDto()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete device", description = "Deletes a device by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Device deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Device not found",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "400", description = "Cannot delete device in use",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))})
    })
    public ResponseEntity<Void> deleteDevice(
            @Parameter(description = "Device ID") @PathVariable Long id) {
        // TODO: Implement
        return ResponseEntity.noContent().build();
    }

}
