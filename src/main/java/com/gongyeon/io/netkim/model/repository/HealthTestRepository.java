package com.gongyeon.io.netkim.model.repository;

import com.gongyeon.io.netkim.model.entity.HealthTestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthTestRepository extends JpaRepository<HealthTestEntity, Integer> {
}
