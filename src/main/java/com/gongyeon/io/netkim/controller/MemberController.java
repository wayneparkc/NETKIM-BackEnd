package com.gongyeon.io.netkim.controller;

import com.gongyeon.io.netkim.model.dto.Member;
import com.gongyeon.io.netkim.model.service.MemberService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@Tag(name = "사용자 관리", description = "일반 사용자의 정보를 관리함.")
@RequestMapping("/api-member")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @Hidden
    @Operation(summary="Security 적용 확인", description="테스트용 메서드 입니다.")
    @GetMapping("")
    public ResponseEntity<String> connectTest() {
        return new ResponseEntity<>("SpringSecurity 적용 예외 확인", HttpStatus.OK);
    }

    @Operation(summary="회원가입", description = "회원가입 요청")
    @PostMapping("/join")
    public ResponseEntity<Void> join(@RequestBody Member member) {
        memberService.signup(member);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary="id 중복 확인", description = "아이디 중복확인 메서드")
    @PostMapping("/id-check")
    public ResponseEntity<Void> idCheck(@RequestBody Member member) {
        if(!memberService.existsByMemberId(member.getEmail())){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Operation(summary="권한 상승 요청", description="사용자들의 권한을 관리")
    @PostMapping("/role-manager")
    public ResponseEntity<Void> roleManager(@RequestHeader HttpHeaders headers, @RequestPart("certificate") MultipartFile certificate, @RequestPart(value = "company", required = false) String company) {
        try {
            if(company==null)
                memberService.upgradePlease(headers, certificate);
            else
                memberService.upgradePlease(headers, certificate, company);
        } catch (FileNotFoundException e) {
            // 이미지 파일이 없을 때
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (BadRequestException exception) {
            // 파일 저장 중 오류가 발생했을 때
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary="메일 인증 요청", description="사용자가 메일 인증을 요청한 경우 실행되는 메서드, 메일을 보내지 못한 경우 403, 메일 인증이 이미 완료된 경우 400, 이외 에러는 404 발생")
    @PostMapping("/verify")
    public ResponseEntity<Void> certify(@RequestHeader HttpHeaders headers) {
        try {
            if (memberService.certify(headers))
                return new ResponseEntity<>(HttpStatus.OK);
        } catch (MessagingException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(summary="메일 인증 확인", description="backend 기록용 -> 프론트는 존재만 확인하면 됨.")
    @GetMapping("/verify")
    public ResponseEntity<String> makeCertify(@RequestParam(name="email") String email, @RequestParam(name = "vnumber") String vnumber, HttpServletResponse response) throws IOException {
       if(memberService.verifyMail(email, vnumber)) {
           response.sendRedirect("https://netkim.vercel.app/");
           return new ResponseEntity<>("인증이 완료되었습니다. 이 창을 종료해주시기 바랍니다.", HttpStatus.OK);
       }
       return new ResponseEntity<>("잘못된 요청입니다. 다시 시도해주시기 바랍니다.", HttpStatus.BAD_REQUEST);
    }

    @Operation(summary = "등업 요청 여부 확인")
    @GetMapping("/check-level")
    public ResponseEntity<Void> checkLevel(@RequestHeader HttpHeaders headers) {
        try {
            memberService.checkLevel(headers);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch(BadRequestException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch(NullPointerException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
}