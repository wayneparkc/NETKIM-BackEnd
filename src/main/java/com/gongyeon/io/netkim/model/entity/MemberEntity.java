package com.gongyeon.io.netkim.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name="member")
@Getter
@Setter
@NoArgsConstructor
public class MemberEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long memberIdx;

    @Column(nullable=false)
    private String password;

    @Column(nullable=false, unique=true)
    private String phone;

    @Column(nullable=false)
    private String memberName;

    @Column(nullable=false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false, unique=true)
    private String email;

    @Column
    private String profileImg;

    @Column
    private String certificateImg;

    @Column
    private String company;

    @Column
    private boolean isCertify;  // 메일 인증 여부 확인

    @CreationTimestamp
    @Column
    private LocalDateTime signupDate;   // 회원가입 일시

    @UpdateTimestamp
    @Column
    private LocalDateTime updateDate;   // 업데이트 일시

    @Builder
    public MemberEntity(String password, String email, String phone, String memberName, Role role, boolean isCertify) {
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.memberName = memberName;
        this.role = role;
        this.isCertify = isCertify;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}