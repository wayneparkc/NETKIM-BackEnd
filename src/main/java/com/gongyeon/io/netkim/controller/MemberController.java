package com.gongyeon.io.netkim.controller;

import com.gongyeon.io.netkim.model.dto.Member;
import com.gongyeon.io.netkim.model.entity.MemberEntity;
import com.gongyeon.io.netkim.model.jwt.JwtUtil;
import com.gongyeon.io.netkim.model.repository.MemberRepository;
import com.gongyeon.io.netkim.model.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.BadRequestException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@Tag(name = "사용자 관리", description = "하하하하")
@RequestMapping("/api-member")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("")
    public ResponseEntity<String> connectTest() {
        return new ResponseEntity<>("SpringSecurity 적용 예외 확인", HttpStatus.OK);
    }

    @Operation(description = "회원가입 요청")
    @PostMapping("/join")
    public ResponseEntity<Void> join(@RequestBody Member member) {
        String token = memberService.signup(member);
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(token);
//        return new ResponseEntity<>(headers, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(description = "아이디 중복확인 메서드")
    @PostMapping("/id-check")
    public ResponseEntity<Void> idCheck(@RequestBody Member member) {
        if(!memberService.existsByMemberId(member.getEmail())){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Operation(description = "권한 상승 요청")
    @PostMapping("/role-manager")
    public ResponseEntity<Void> roleManager(@RequestHeader HttpHeaders headers, @RequestPart("certificate") MultipartFile certificate) throws IOException {
        try {
            memberService.upgradePlease(headers, certificate);
        } catch (FileNotFoundException e) {
            // 이미지 파일이 없을 때
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (ChangeSetPersister.NotFoundException e) {
            // 사용자를 찾을 수 없을 때
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (BadRequestException exception) {
            // 파일 저장 중 오류가 발생했을 때
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "메일 인증 요청 하기")
    @PostMapping("/verify")
    public ResponseEntity<Void> certify(@RequestHeader HttpHeaders headers) throws IOException {
        if(memberService.certify(headers))
            return new ResponseEntity<>(HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Operation(summary="메일 인증확인하기")
    @GetMapping("/verify")
    public ResponseEntity<?> makeCertify(@RequestParam(name="email") String email, @RequestParam(name = "vnumber") String vnumber) {
       if(memberService.verifyMail(email, vnumber))
            return new ResponseEntity<>(HttpStatus.OK);
       return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}