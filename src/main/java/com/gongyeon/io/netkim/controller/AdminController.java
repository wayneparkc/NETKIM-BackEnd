package com.gongyeon.io.netkim.controller;

import com.gongyeon.io.netkim.model.entity.MemberEntity;
import com.gongyeon.io.netkim.model.repository.MemberRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api-admin")
public class AdminController {
    private final MemberRepository memberRepository;

    public AdminController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Operation(description="권한 부여 신청자를 확인하기 위한 메서드")
    @GetMapping("/")
    public ResponseEntity<List<MemberEntity>> getAuthorizationList(){
        List<MemberEntity> memberList = memberRepository.getLevelUpMembers();
        if(memberList==null || memberList.isEmpty()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(memberList, HttpStatus.OK);
    }

    @Operation(description="권한 부여를 위한 메서드")
    @PostMapping("/")
    public ResponseEntity<Void> setAuthorization(@RequestBody Map<String, String> map){
        MemberEntity member = memberRepository.findByMemberIdx(Integer.parseInt(map.get("memberIdx")));
        
        return ResponseEntity.ok().build();
    }
}
