package com.gongyeon.io.netkim.model.repository;

import com.gongyeon.io.netkim.model.entity.ReporterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReporterRepository extends JpaRepository<ReporterEntity, Integer> {
    // Performance에 저장된 Fcltynm으로 조회할 때 필요한 메서드
    List<ReporterEntity> findAllByMemberIdx(long memberIdx);
    ReporterEntity findByReporterId(long reporterId);
    ReporterEntity findByEmailAndMemberIdx(String Email, long memberIdx);
    @Modifying
    @Query(value = "delete from reporter WHERE reporter_id=?1 AND member_idx=?2", nativeQuery = true)
    void deleteByReporterIdAndMemberIdx(long reporterId, long memberIdx);
}