package com.gongyeon.io.netkim.model.service;

import com.gongyeon.io.netkim.model.dto.Member;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface MemberService extends UserDetailsService {
    String insert(Member member);
    boolean existsByMemberId(String memberId);
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
