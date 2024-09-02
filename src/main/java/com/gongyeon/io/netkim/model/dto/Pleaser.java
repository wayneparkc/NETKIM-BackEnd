package com.gongyeon.io.netkim.model.dto;

import com.gongyeon.io.netkim.model.entity.MemberEntity;
import com.gongyeon.io.netkim.model.entity.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pleaser {
    private long memberIdx;
    private String memberName;
    private String certificateImg;
    private Role role;
    private String company;

    // Constructor
    public Pleaser(MemberEntity member) {
        this.memberIdx = member.getMemberIdx();
        this.memberName = member.getMemberName();
        this.certificateImg = member.getCertificateImg();
        this.role = member.getRole();
        this.company = member.getCompany();
    }
}
