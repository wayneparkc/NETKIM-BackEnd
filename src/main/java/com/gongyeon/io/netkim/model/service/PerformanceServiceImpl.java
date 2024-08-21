package com.gongyeon.io.netkim.model.service;

import com.gongyeon.io.netkim.model.entity.PerformanceEntity;
import com.gongyeon.io.netkim.model.repository.PerformanceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PerformanceServiceImpl implements PerformanceService {
    private PerformanceRepository performanceRepository;

    public PerformanceServiceImpl(PerformanceRepository performanceRepository) {
        this.performanceRepository = performanceRepository;
    }

    @Override
    public List<PerformanceEntity> getAllPerformance() {
        return performanceRepository.findAll();
    }

    @Override
    public PerformanceEntity getDetail(long prfId) {
        PerformanceEntity performance = performanceRepository.findByPrfid(prfId);
        // 기존에 상세조회가 되어 있는 경우에는 Query를 던질 필요가 없지만, 그렇지 않다면 Query를 다시 던진다.
        // 던지는 방법에는 여러가지가 있지만, 어떤 방식으로 던질지에 대해서는 고민이 필요하다.
        if(performance.getPrfcast()==null) {

        }
        return null;
    }
}