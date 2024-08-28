package com.gongyeon.io.netkim.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name="reporter")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ReporterEntity {
    // 관리 index
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long reporterId;

    // 기자 이름
    @Column(length=10)
    private String reporterName;

    // 이메일 주소
    @Column
    private String email;

    // 언론사
    @Column
    private String press;

    // 등록한 사람의 index
    @Column
    private long memberIdx;

    // 구분
    @Column
    private String kindOf;
}
