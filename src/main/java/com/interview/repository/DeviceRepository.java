package com.interview.repository;

import com.interview.entity.DeviceEntity;
import com.interview.enums.DeviceState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface to handle database operations with {@link com.interview.entity.DeviceEntity}.
 */
@Repository
public interface DeviceRepository extends JpaRepository<DeviceEntity, Long> {

    @Query("SELECT d FROM DeviceEntity d WHERE LOWER(d.brand) = LOWER(:brand) AND d.state = :state")
    Page<DeviceEntity> findByBrandIgnoreCaseAndState(@Param("brand") String brand, @Param("state") DeviceState state, Pageable pageable);

    Page<DeviceEntity> findByBrandIgnoreCase(String brand, Pageable pageable);

    Page<DeviceEntity> findByState(DeviceState state, Pageable pageable);

}
