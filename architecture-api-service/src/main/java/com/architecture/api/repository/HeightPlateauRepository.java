package com.architecture.api.repository;

import com.architecture.api.model.HeightPlateau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeightPlateauRepository extends JpaRepository<HeightPlateau, Long> {
}
