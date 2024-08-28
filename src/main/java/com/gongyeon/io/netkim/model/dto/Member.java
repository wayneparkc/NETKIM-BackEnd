package com.gongyeon.io.netkim.model.dto;

import com.gongyeon.io.netkim.model.entity.MemberEntity;
import lombok.Data;

@Data
public class Member {
    private String username;
    private String phone;
    private String password;
    private String email;

    public MemberEntity toEntity() {
        return MemberEntity.builder()
                .password(password)
                .memberName(username)
                .phone(phone)
                .email(email)
                .isCertify(false)
                .build();
    }
}
