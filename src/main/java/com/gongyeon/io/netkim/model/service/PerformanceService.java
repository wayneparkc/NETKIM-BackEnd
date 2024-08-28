package com.gongyeon.io.netkim.model.service;

import com.gongyeon.io.netkim.model.entity.PerformanceEntity;

import java.util.List;

public interface PerformanceService {
    List<PerformanceEntity> getAllPerformance();
    List<String> getAllprfnm();
    PerformanceEntity getDetail(String kopisId);
    int insertPerformance();
    PerformanceEntity updatePerformance(String kopisId);

}
