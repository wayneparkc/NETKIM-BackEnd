package com.gongyeon.io.netkim.model.repository;

import com.gongyeon.io.netkim.model.entity.PerformanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformanceRepository extends JpaRepository<PerformanceEntity, Integer> {
    PerformanceEntity findByKopisId(String kopisId);
    PerformanceEntity findByPrfid(long prfId);
    @Query(value = "select prfnm from performance", nativeQuery = true)
    List<String> findPrfnmList();
    PerformanceEntity findByPrfnm(String prfnm);
}