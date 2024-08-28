package com.gongyeon.io.netkim.model.entity;

/**
  역할을 정의한 클래스
    - 관리자 계정 : ADMIN
    - 사업자 인증이 된 계정 : MANAGER
    - 회원가입만 완료 된 계정 : MEMBER
*/

public enum Role {
    ADMIN, MANAGER, MEMBER
}
