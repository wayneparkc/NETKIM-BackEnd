package com.gongyeon.io.netkim.model.service;

import com.gongyeon.io.netkim.model.entity.PerformanceEntity;

import java.util.List;

public interface PerformanceService {
    List<PerformanceEntity> getAllPerformance();
    PerformanceEntity getDetail(long prfId);

}
