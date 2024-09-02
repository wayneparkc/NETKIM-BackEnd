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
    private long pressReleaseId;                      // 식별 index

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="prfId")
    private PerformanceEntity performance;  // KOPIS 관리 ID

    @Column
    private long memberIdx;    		// 작성자 인덱스

    @Column
    private String headLine;                // 보도자료 제목

    @Column(length = 4000)
    private String content;                 // 보도자료 내용

    @Column
    private String filename;
}
