package com.gongyeon.io.netkim.model.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@Entity
@Table(name="performance")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PerformanceEntity {
	/*	기본 명세
	공연(Performance): prf
	이름(Name): nm
	날짜(Day): d
	공연장(Faculty): fclty
	*/
    // Field
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long prfid;				//공연 ID (Primary Key) | mt20id

    @Column
    private String prfnm;				//공연 이름

    @Column
    private String prfstate;			//공연상태(공연완료, 공연중, 공연완료)

    @Column
    private LocalDate prfdfrom;			//공연시작일

    @Column
    private LocalDate prfdto;			//공연종료일

    @UpdateTimestamp
    @Column
    private LocalDateTime updateDate;   // 업데이트 일시

    @Column
    private String poster;				//포스터 경로

    @OneToOne
    @JoinColumn(name="theaterId")
    private TheaterEntity fcltynm;			//공연장

    @OneToMany
    @JoinColumn(name="actorId")
    private ArrayList<ActorEntity> prfcast;	//공연 캐스트 (직접 입력 필요)

    @Column
    private boolean openrun;			//오픈런 여부

    @Column
    private LocalTime prfruntime;		//공연시간

    @Column
    private String feature;	//공연 특징

    // 공연 추천에 적용할 수 있는 특성
    // 1. 공연의 장르의 구분이 필요하다.
//    @Column
//    private String genre;				//장르명 | genrenm

    // 2. (해당 공연이 원어공연 내한인지, 라이센스인지, 국내 창작인지 구분)
//    @Column
//    private boolean visit;				//내한 여부

//    @Column
//    private boolean license;			//라이센스 여부

//    @Column
//    private boolean createprf;			//창작공연 여부

    // 3. 제작, 창작진의 정보가 필요한가? 결정 필요
//    private String prfcrew;				//제작진

    // 공연 특징 제공 시 StringType으로 저장된 것 Parsing 하여 제공
    // 제1정규화에 위배되나, DB 관리의 용이성을 위해 역정규화 실시
    public List<String> getFeature() {
        List<String> FeatureList = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(this.feature, ",");
        while(st.hasMoreTokens()) {
            FeatureList.add(st.nextToken());
        }
        return FeatureList;
    }
}