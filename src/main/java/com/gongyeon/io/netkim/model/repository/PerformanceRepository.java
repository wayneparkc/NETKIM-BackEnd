package com.gongyeon.io.netkim.model.repository;

import com.gongyeon.io.netkim.model.entity.PerformanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformanceRepository extends JpaRepository<PerformanceEntity, Integer> {
    PerformanceEntity findByPrfid(long prfId);
    List<String> findAllPrfNm();
}