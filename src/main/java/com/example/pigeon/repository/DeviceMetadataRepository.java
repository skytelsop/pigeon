package com.example.pigeon.repository;

import com.example.pigeon.model.DeviceMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceMetadataRepository extends JpaRepository<DeviceMetadata, Long> {
    List<DeviceMetadata> findByUserId(Long userId);
}
