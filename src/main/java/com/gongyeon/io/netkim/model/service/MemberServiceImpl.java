package com.gongyeon.io.netkim.model.service;

import com.gongyeon.io.netkim.model.dto.Member;
import com.gongyeon.io.netkim.model.entity.MemberEntity;
import com.gongyeon.io.netkim.model.entity.Role;
import com.gongyeon.io.netkim.model.jwt.JwtUtil;
import com.gongyeon.io.netkim.model.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberServiceImpl implements MemberService, UserDetailsService {
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    public MemberServiceImpl(MemberRepository memberRepository, JwtUtil jwtUtil) {
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public String insert(Member member) {
        // DB Entity로 변신
        MemberEntity memberEntity = member.toEntity();
        // Role Default 값 지정
        memberEntity.setRole(Role.MEMBER);
        // 비밀번호 암호화
        String encPassword = new BCryptPasswordEncoder().encode(memberEntity.getPassword());
        memberEntity.setPassword(encPassword);
        // 저장하기
        memberRepository.save(memberEntity);
        // 회원가입 완료 이후 토큰 발급 이후 return
        return jwtUtil.createToken(memberEntity.getMemberIdx(), memberEntity.getRole().name());
    }

    @Transactional
    public boolean existsByMemberId(String memberId){
        return memberRepository.existsByMemberId(memberId);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByMemberId(username);
    }
}
