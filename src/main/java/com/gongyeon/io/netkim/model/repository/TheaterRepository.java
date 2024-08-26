package com.gongyeon.io.netkim.model.repository;

import com.gongyeon.io.netkim.model.entity.TheaterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TheaterRepository extends JpaRepository<TheaterEntity, Integer> {
    // Performance에 저장된 Fcltynm으로 조회할 때 필요한 메서드
    TheaterEntity findByFcltynm(String fcltynm);
}
