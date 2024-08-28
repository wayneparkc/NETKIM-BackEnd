package com.gongyeon.io.netkim.model.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class Reporter {
    // 기자 이름
    @Column(length=10)
    private String reporterName;

    // 이메일 주소
    @Column
    private String email;

    // 언론사
    @Column
    private String press;
}
