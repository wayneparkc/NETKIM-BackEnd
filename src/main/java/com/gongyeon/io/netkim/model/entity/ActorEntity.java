package com.gongyeon.io.netkim.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "actor")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long actorId;					// 배우 ID (Primary Key) | mt10id

    @Column
    private String actornm;					// 배우 이름

    // 출연작을 저장할 때, 순환 참조가 발생하지 않도록 Mapping Table을 별도로 설정해야 하는지 확인하기
//    @Column
//    private List<String> filmography;	// 필모그래피(출연작)

    // 수상 내역을 저장할 것인지 정하기
//    @Column
//    private List<String> nominate;          // 수상 내역
}
