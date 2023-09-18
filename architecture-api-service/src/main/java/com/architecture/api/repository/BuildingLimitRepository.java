package com.architecture.api.repository;

import com.architecture.api.model.BuildingLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildingLimitRepository extends JpaRepository<BuildingLimit, Long> {
}
