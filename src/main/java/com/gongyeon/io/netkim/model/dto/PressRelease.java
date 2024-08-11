package com.gongyeon.io.netkim.model.dto;

import lombok.Data;

@Data
public class PressRelease {
    // 보도자료
    private String synopsis;
    // List 형으로 보낼 수 있는지, String으로 입력받아 Parsing해야 하는지 확인하기
    private String actors;
    private double arms;
    private double upper_body;
    // 추가로 들어갈 수 있는 사항
    // 지방공연 여부, 좌석 점유율에 따른 추가 홍보 여부!
}
