package com.gongyeon.io.netkim.model.dto;

import lombok.Data;

/**
  보도자료를 작성하기 위한 기초 자료들을 받는 DTO
    - 주요 결정사항
      - 줄거리 크롤링 할 것인가?
      - 줄거리 이미지를 넣었을 떄 OCR로 적용을 시킬 것인가?
      - 추가로 받아야 하는 Parameter가 있는지
*/

@Data
public class PressRelease {
    // 공연 id
    private long performanceId;

    // 키워드
    private String key;

    // 줄거리
    private String synopsis;

    // List 형으로 보낼 수 있는지, String으로 입력받아 Parsing해야 하는지 확인하기
    private String actors;

    // 현재까지 예매된 좌석 또는 관람객 수 (int)
    private int seats;

    // 인터뷰 대상자
    private String interviewee;

    // 인터뮤 내용
    private String interviewContent;
}
