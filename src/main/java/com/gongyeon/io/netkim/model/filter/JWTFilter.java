package com.gongyeon.io.netkim.model.filter;

import com.gongyeon.io.netkim.model.entity.MemberEntity;
import com.gongyeon.io.netkim.model.jwt.JwtUtil;
import com.gongyeon.io.netkim.model.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

public class JWTFilter extends OncePerRequestFilter {
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    public JWTFilter(MemberRepository memberRepository, JwtUtil jwtUtil) {
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //request에서 Authorization 헤더를 찾음
        String authorization=request.getHeader("Authorization");

        // Authorization 헤더 검증 : Null이거나, 작성한 양식과 일치하지 않으면 종료시킴
        if (authorization == null || !authorization.startsWith("Bearer ")) {

            System.out.println("token null");
            filterChain.doFilter(request, response);

            //조건이 해당되면 메소드 종료 (필수)
            return;
        }

        // Bearer 부분 제거 후 순수 토큰만 획득
        String token = authorization.split(" ")[1];

        // 토큰 소멸 시간 검증
        Jws<Claims> clame = jwtUtil.validate(token);

        if (clame.getPayload().getExpiration().before(new Date())) {
            System.out.println("토큰 만료됨.");

            filterChain.doFilter(request, response);

            return;
        }

        //토큰에서 username과 role 획득
        long memberIdx = jwtUtil.getMemberIdx(token);
        String role = jwtUtil.getRole(token);

        //userEntity를 생성하여 값 set
        MemberEntity memberEntity = memberRepository.findByMemberIdx(memberIdx);
        if (memberEntity==null || !role.equals(memberEntity.getRole().name())) {
            return;
        }

        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(memberEntity, null, memberEntity.getAuthorities());
        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
