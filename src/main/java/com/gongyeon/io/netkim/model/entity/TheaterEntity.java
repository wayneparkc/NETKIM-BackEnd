package com.gongyeon.io.netkim.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="theater")
@Getter
@Setter
@NoArgsConstructor
public class TheaterEntity {
	/*	기본 명세
	공연(Performance): prf
	장소(Place): plc
	공연장(Faculty): fclty	//공연예술통합전산망에서는 해당 용어를 사용하였으나, Theater도 좋을듯
	이름(Name): nm
	날짜(Day): d
	*/
    @Builder
    public TheaterEntity(String fcltynm, int seatCnt, String plcadres){
        this.fcltynm = fcltynm;
        this.seatCnt = seatCnt;
        setFcltyscale();
        this.plcadres = plcadres;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long plcid;			//공연장 ID (Primary Key) | mt10id

    @Column
    private String fcltynm;			//공연장 이름

    @Column
    private int seatCnt;			//객석수

    @Column
    private String fcltyscale;		//공연장 규모 (Getter에서 입력 값이 아닌 자동으로 결정 | 500석 미만)

    // 공연장 주소를 도로명 주소로 받는것이 좋을지, 기타 Map과의 연계성을 고려하여 경도 위도로 받을지 결정 필요
    @Column
    private String plcadres;		//공연장 주소

    public void setFcltyscale() {
        if(this.seatCnt == 0) {
            return;
        }else if(this.seatCnt >=1000) {
            this.fcltyscale = "대극장";
        }else if(this.seatCnt <=300){
            this.fcltyscale = "소극장";
        }else {
            this.fcltyscale = "중극장";
        }
    }
}
