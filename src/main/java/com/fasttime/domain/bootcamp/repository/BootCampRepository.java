package com.fasttime.domain.bootcamp.repository;

import com.fasttime.domain.bootcamp.entity.BootCamp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BootCampRepository extends JpaRepository<BootCamp, Long> {
    boolean existsByName(String name);
}
