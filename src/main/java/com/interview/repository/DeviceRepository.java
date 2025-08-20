package com.interview.repository;

import com.interview.entity.DeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface to handle database operations with {@link com.interview.entity.DeviceEntity}.
 */
@Repository
public interface DeviceRepository extends JpaRepository<DeviceEntity, Long> {
}
