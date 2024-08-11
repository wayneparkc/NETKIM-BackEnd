package com.gongyeon.io.netkim.model.dto;

import com.gongyeon.io.netkim.model.entity.MemberEntity;
import lombok.Data;

@Data
public class Member {
    private String username;
    private String phone;
    private String userId;
    private String password;

    public MemberEntity toEntity() {
        return MemberEntity.builder()
                .memberName(username)
                .phone(phone)
                .memberId(userId)
                .password(password)
                .build();
    }
}
