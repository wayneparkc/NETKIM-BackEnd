package com.gongyeon.io.netkim.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="pressrelease")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PressReleaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long pRId;                      // 식별 index

    @OneToOne
    @JoinColumn(name="prfId")
    private PerformanceEntity performance;  // KOPIS 관리 ID

    @OneToOne
    @JoinColumn(name="memberIdx")
    private MemberEntity member;    		// 작성자 이름

    @Column(columnDefinition = "TEXT")
    private String content;                 // 보도자료 내용
}
