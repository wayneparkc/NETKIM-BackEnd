package com.gongyeon.io.netkim.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
  추후 Blue, Green 배포까지 진행하기 위한 상태 확인 Class
*/

@Entity
@Table(name="healthtest")
@NoArgsConstructor
@Getter
@Setter
public class HealthTestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 100)
    private String text;

    @Column(length = 30)
    private LocalDateTime wtime;

    @Builder
    public HealthTestEntity(String text) {
        this.text = text;
        this.wtime = LocalDateTime.now();
    }
}
