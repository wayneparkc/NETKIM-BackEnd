package com.gongyeon.io.netkim.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
    private int memberIdx;

    @Column(nullable = false, unique=true)
    private String memberId;

    @Column(nullable=false)
    private String password;

    @Column(nullable=false, unique=true)
    private String phone;

    @Column(nullable=false)
    private String memberName;

    @Column(nullable=false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column
    private String profileImg;

    @Column
    private String certificateImg;

    @Column
    private String company;

    @Builder
    public MemberEntity(String memberId, String password, String phone, String memberName, Role role) {
        this.memberId = memberId;
        this.password = password;
        this.phone = phone;
        this.memberName = memberName;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return this.memberId;
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