package com.gongyeon.io.netkim.controller;

import com.gongyeon.io.netkim.model.dto.Member;
import com.gongyeon.io.netkim.model.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        System.out.println("실행중");
        String token = memberService.insert(member);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @Operation(description = "아이디 중복확인 메서드")
    @PostMapping("/id-check")
    public ResponseEntity<Void> idCheck(@RequestBody Member member) {
        if(!memberService.existsByMemberId(member.getUserId())){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}