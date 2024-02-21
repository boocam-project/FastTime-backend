package com.fasttime.domain.reference.repository;

import com.fasttime.domain.reference.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

}
