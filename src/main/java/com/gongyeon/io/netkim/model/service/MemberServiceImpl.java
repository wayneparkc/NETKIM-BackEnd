package com.gongyeon.io.netkim.model.service;

import com.gongyeon.io.netkim.model.dto.Member;
import com.gongyeon.io.netkim.model.entity.MemberEntity;
import com.gongyeon.io.netkim.model.entity.Role;
import com.gongyeon.io.netkim.model.jwt.JwtUtil;
import com.gongyeon.io.netkim.model.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService, UserDetailsService {
    private final MemberRepository memberRepository;
    private final MailService mailService;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;

    @Override
    public String signup(Member member) {
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

    @Override
    @Transactional
    public boolean existsByMemberId(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByEmail(username);
    }

    @Override
    public void upgradePlease(HttpHeaders headers, MultipartFile certificate) throws IOException, NotFoundException {
        if(certificate.isEmpty() || certificate.getSize()==0) {
            throw new FileNotFoundException();
        }

        long memberIdx = jwtUtil.getMemberIdx(headers.getFirst("Authorization").split(" ")[1]);
        MemberEntity member = memberRepository.findByMemberIdx(memberIdx);

        if(member == null) {
            throw new NotFoundException();
        }

        File videoFolder = new File("data/certificates/");
        if (!videoFolder.exists()) {
            videoFolder.mkdir();
        }

        // 중복 없이 서버에서 파일을 찾을 수 있도록 설정하기
        String today = Long.toString(System.currentTimeMillis());

        String fileId = today + "_" + certificate.getOriginalFilename();
        member.setCertificateImg(fileId);

        System.out.println(videoFolder.getAbsolutePath());
        certificate.transferTo(new File(videoFolder.getAbsolutePath(), fileId));

        memberRepository.save(member);
    }

    @Override
    public boolean certify(HttpHeaders headers) {
        long memberIdx = jwtUtil.getMemberIdx(headers.getFirst("Authorization").split(" ")[1]);
        MemberEntity member = memberRepository.findByMemberIdx(memberIdx);
        if(member == null || member.isCertify()) {
            return false;
        }
        String title = "공연이요 이메일 인증 안내";
        String authCode = this.createCode();
        String content = String.format(
        """
           <!DOCTYPE html>
               <html lang="en">
                   <head>
                       <meta charset="UTF-8">
                       <style>
                        a{
                            display: block;
                            align: center;
                            background-color: #f97272;
                        }
                       </style>
                   </head>
                   <body>
                     <a href='%sapi-member/verify?vnumber=%s&email=%s'> 메일 인증하기 </a>
                   </body>
               </html>
        """, "https://gongyeon.kro.kr/", authCode, member.getEmail());

        mailService.sendEmail(member.getEmail(), title, content);
        saveCertificationNumber(member.getEmail(), authCode);
        return true;
    }

    private void saveCertificationNumber(String email, String certificationNumber) {
        redisTemplate.opsForValue()
                .set(email, certificationNumber,
                        Duration.ofMinutes(30));
    }

    @Override
    public boolean verifyMail(String email, String certificationNumber) {
        if(hasKey(email) && getCertificationNumber(email).equals(certificationNumber)){
            MemberEntity member = memberRepository.findByEmail(email);
            member.setCertify(true);
            memberRepository.save(member);
            removeCertificationNumber(email);
        }
        return memberRepository.findByEmail(email).isCertify();
    }

    private String getCertificationNumber(String email) {
        return redisTemplate.opsForValue().get(email);
    }

    private void removeCertificationNumber(String email) {
        redisTemplate.delete(email);
    }

    private boolean hasKey(String email) {
        Boolean keyExists = redisTemplate.hasKey(email);
        return keyExists != null && keyExists;
    }

    private String createCode() {
        int length = 6;
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "971030";
        }
    }
}
