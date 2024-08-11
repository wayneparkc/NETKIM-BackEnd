package com.gongyeon.io.netkim.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="healthtest")
@NoArgsConstructor
@Getter
@Setter
public class HealthTestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 255)
    private String text;

    @Column(length = 30)
    private LocalDateTime wtime;

    @Builder
    public HealthTestEntity(String text) {
        this.text = text;
        this.wtime = LocalDateTime.now();
    }
}
