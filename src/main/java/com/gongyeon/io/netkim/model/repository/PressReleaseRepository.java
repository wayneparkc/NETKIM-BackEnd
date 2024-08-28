package com.gongyeon.io.netkim.model.repository;

import com.gongyeon.io.netkim.model.entity.PressReleaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PressReleaseRepository extends JpaRepository<PressReleaseEntity, Integer> {
    List<PressReleaseEntity> findByMemberIdx(long memberIdx);
    PressReleaseEntity findByPressReleaseId(long pressReleaseId);
}
