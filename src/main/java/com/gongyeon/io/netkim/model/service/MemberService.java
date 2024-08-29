package com.gongyeon.io.netkim.model.service;

import com.gongyeon.io.netkim.model.dto.Member;
import jakarta.mail.MessagingException;
import org.apache.coyote.BadRequestException;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MemberService extends UserDetailsService {
    void signup(Member member);
    boolean existsByMemberId(String memberId);
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    void upgradePlease(HttpHeaders headers, MultipartFile certificate) throws IOException, NotFoundException;
    boolean certify(HttpHeaders headers) throws MessagingException, BadRequestException;
    boolean verifyMail(String email, String cNumber);
}