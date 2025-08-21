package com.interview.repository;

import com.interview.entity.DeviceEntity;
import com.interview.enums.DeviceState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeviceRepositoryTest extends PostgresContainerInitializer {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DeviceRepository deviceRepository;

    @BeforeEach
    void init() {
        DeviceEntity device1 = new DeviceEntity("iPhone 15", "Apple", DeviceState.AVAILABLE);
        DeviceEntity device2 = new DeviceEntity("Galaxy S24", "Samsung", DeviceState.AVAILABLE);
        DeviceEntity device3 = new DeviceEntity("Pixel 9", "Google", DeviceState.IN_USE);
        DeviceEntity device4 = new DeviceEntity("Pixel 9 Pro", "google", DeviceState.INACTIVE);

        entityManager.persist(device1);
        entityManager.persist(device2);
        entityManager.persist(device3);
        entityManager.persist(device4);
    }

    @Test
    void findAll_ReturnsDevices() {
        Page<DeviceEntity> availableDevices = deviceRepository.findAll(Pageable.ofSize(10));

        assertEquals(4, availableDevices.getTotalElements());
    }

    @Test
    void findByBrandAndState_ReturnsDevices() {
        Page<DeviceEntity> availableDevices = deviceRepository.findByBrandIgnoreCaseAndState("GOOGLE", DeviceState.IN_USE, Pageable.ofSize(10));

        assertEquals(1, availableDevices.getTotalElements());
        assertEquals("Pixel 9", availableDevices.getContent().getFirst().getName());
    }


    @Test
    void findByBrand_ReturnsDevices() {
        Page<DeviceEntity> availableDevices = deviceRepository.findByBrandIgnoreCase("GOOGLE", Pageable.ofSize(10));

        assertEquals(2, availableDevices.getTotalElements());
        assertTrue(availableDevices.stream().allMatch(d -> d.getBrand().equalsIgnoreCase("Google")));
    }

    @Test
    void findByState_ReturnsDevices() {
        Page<DeviceEntity> availableDevices = deviceRepository.findByState(DeviceState.AVAILABLE, Pageable.ofSize(10));

        assertEquals(2, availableDevices.getTotalElements());
        assertTrue(availableDevices.stream().allMatch(d -> d.getState() == DeviceState.AVAILABLE));
    }
}
