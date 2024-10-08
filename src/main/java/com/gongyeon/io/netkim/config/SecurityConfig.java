package com.gongyeon.io.netkim.config;

import com.gongyeon.io.netkim.model.entity.Role;
import com.gongyeon.io.netkim.model.filter.JWTFilter;
import com.gongyeon.io.netkim.model.filter.LoginFilter;
import com.gongyeon.io.netkim.model.jwt.JwtUtil;
import com.gongyeon.io.netkim.model.repository.MemberRepository;
import com.gongyeon.io.netkim.model.service.MemberService;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final MemberService memberService;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JwtUtil jwtUtil, MemberService memberService) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
        this.memberService = memberService;
    }

    // 인가 관리
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(memberService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authenticationProvider);
    }

    // 회원가입 시 비밀번호 암호화를 위한 BCryptPasswordEncoder 생성
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    
    // 로그인 등 SpringSecurity 설정 중
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, DispatcherServletAutoConfiguration dispatcherServletAutoConfiguration, MemberRepository memberRepository) throws Exception {
        http.
                csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((auth) -> auth
//                // swagger 설정
                        .requestMatchers("/swagger", "/swagger-ui.html", "/swagger-ui/**", "/api-docs", "/api-docs/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/", "/env", "/api-member/**", "/api-file/**").permitAll()
//                // Spring Security Filter 적용하기
                        .requestMatchers("/api-admin/**").hasAuthority(Role.ADMIN.name())
                        .requestMatchers("/api-reporter/**", "/api-news/**").hasAnyAuthority(Role.ADMIN.name(), Role.MANAGER.name())
                        .anyRequest().authenticated())
                .addFilterBefore(new JWTFilter(memberRepository, jwtUtil), LoginFilter.class)
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, "/api-member/login"), UsernamePasswordAuthenticationFilter.class)
                .logout((logout)-> logout.logoutUrl("/api-member/logout"))
                .sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // OAuth 2.0 로그인 방식 설정
//        http
//                .oauth2Login((auth) -> auth.loginPage("/oauth-login/login")
//                        .defaultSuccessUrl("/oauth-login")
//                        .failureUrl("/oauth-login/login")
//                        .permitAll());
        return http.build();
    }
}