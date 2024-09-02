package com.gongyeon.io.netkim.model.service;

import com.gongyeon.io.netkim.model.dto.Member;
import com.gongyeon.io.netkim.model.entity.MemberEntity;
import com.gongyeon.io.netkim.model.entity.Role;
import com.gongyeon.io.netkim.model.jwt.JwtUtil;
import com.gongyeon.io.netkim.model.repository.MemberRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
import java.util.StringTokenizer;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService, UserDetailsService {
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;
    private final JavaMailSender mailSender;

    @Override
    public void signup(Member member) {
        // DB Entity로 변신
        MemberEntity memberEntity = member.toEntity();
        // Role Default 값 지정
        memberEntity.setRole(Role.MEMBER);
        // 비밀번호 암호화
        String encPassword = new BCryptPasswordEncoder().encode(memberEntity.getPassword());
        memberEntity.setPassword(encPassword);
        // 저장하기
        memberRepository.save(memberEntity);
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
    @Transactional
    public void upgradePlease(HttpHeaders headers, MultipartFile certificate) throws IOException {
        if(certificate.isEmpty() || certificate.getSize()==0) {
            throw new FileNotFoundException();
        }

        long memberIdx = jwtUtil.getMemberIdx(headers.getFirst("Authorization").split(" ")[1]);
        MemberEntity member = memberRepository.findByMemberIdx(memberIdx);
        if(!member.getRole().equals(Role.MEMBER)) {
            throw new BadRequestException();
        }

        File videoFolder = new File("data/certificates/");
        if (!videoFolder.exists()) {
            videoFolder.mkdir();
        }

        // 중복 없이 서버에서 파일을 찾을 수 있도록 설정하기
        String today = Long.toString(System.currentTimeMillis());

        StringTokenizer st = new StringTokenizer(certificate.getOriginalFilename(), ".");

        String extension = "";
        while(st.hasMoreTokens()) {
            extension = st.nextToken();
        }

        String fileId = today + "_cert." + extension;
        member.setCertificateImg("https://gongyeon.kro.kr/api-file/cert/"+fileId);

        System.out.println(videoFolder.getAbsolutePath());
        certificate.transferTo(new File(videoFolder.getAbsolutePath(), fileId));

        memberRepository.save(member);
    }

    @Override
    @Transactional
    public void upgradePlease(HttpHeaders headers, MultipartFile certificate, String company) throws IOException {
        if(certificate.isEmpty() || certificate.getSize()==0) {
            throw new FileNotFoundException();
        }

        long memberIdx = jwtUtil.getMemberIdx(headers.getFirst("Authorization").split(" ")[1]);
        MemberEntity member = memberRepository.findByMemberIdx(memberIdx);
        member.setCompany(company);
        if(!member.getRole().equals(Role.MEMBER)) {
            throw new BadRequestException();
        }

        File videoFolder = new File("data/certificates/");
        if (!videoFolder.exists()) {
            videoFolder.mkdir();
        }

        // 중복 없이 서버에서 파일을 찾을 수 있도록 설정하기
        String today = Long.toString(System.currentTimeMillis());

        StringTokenizer st = new StringTokenizer(certificate.getOriginalFilename(), ".");

        String extension = "";
        while(st.hasMoreTokens()) {
            extension = st.nextToken();
        }

        String fileId = today + "_cert." + extension;
        member.setCertificateImg("https://gongyeon.kro.kr/api-file/cert/"+fileId);

        System.out.println(videoFolder.getAbsolutePath());
        certificate.transferTo(new File(videoFolder.getAbsolutePath(), fileId));

        memberRepository.save(member);
    }

    @Override
    public boolean certify(HttpHeaders headers) throws MessagingException, BadRequestException {
        long memberIdx = jwtUtil.getMemberIdx(headers.getFirst("Authorization").split(" ")[1]);
        MemberEntity member = memberRepository.findByMemberIdx(memberIdx);

        if(member == null || member.isCertify()) {
            throw new BadRequestException("이미 인증이 완료된 회원입니다.");
        }

        String title = "공연이요 이메일 인증 안내";
        String authCode = this.createCode();
        String content = String.format(
           """
             <h1>공연이요 회원 이메일 인증</h1>
             <p>이메일 인증 거부하실 수 있으나, 보도자료 발송 등 이메일을 활용한 다양한 서비스의 접근이 제한될 수 있습니다.</p>
             <p>인증을 희망하시는 경우 아래의 버튼을 클릭해주십시오.</p>
             <div style="border-radius:4px;background-color:#2196f3;display:block;color:#fff;line-height:26px;padding:8px 22px;margin-top:20px;text-align:center">
                <a href='%sapi-member/verify?vnumber=%s&email=%s' style="text-decoration:none;color:RGB(255,255,255);">
                    <b>메일 인증하기</b>
                </a>
             </div>
            """, "https://gongyeon.kro.kr/", authCode, member.getEmail());

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom("admin@gongyeon.kro");
        helper.setTo(member.getEmail());
        helper.setSubject(title);
        helper.setText(content, true);
        mailSender.send(mimeMessage);
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

    @Override
    public void checkLevel(HttpHeaders headers) throws BadRequestException {
        long memberIdx = jwtUtil.getMemberIdx(headers.getFirst("Authorization").split(" ")[1]);
        MemberEntity member = memberRepository.findByMemberIdx(memberIdx);
        if(!Role.MEMBER.name().equals(member.getRole().name())){
            throw new BadRequestException("이미 등업된 회원입니다.");
        }else if(member.getCertificateImg()!=null) {
            throw new NullPointerException("검증이 진행중인 회원입니다.");
        }
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
            System.out.println("이메일 인증 코드 생성 중 오류 발생"+e);
            return "971030";
        }
    }
}
