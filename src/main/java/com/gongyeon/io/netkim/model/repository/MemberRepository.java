package com.gongyeon.io.netkim.model.repository;

import com.gongyeon.io.netkim.model.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Integer> {
    // id 중복체크
    boolean existsByMemberId(String memberId);
    // 이름으로 찾기
    MemberEntity findByMemberId(String memberId);
    // member id로 찾기
    MemberEntity findByMemberIdx(int memberIdx);
}