package com.gongyeon.io.netkim.controller;

import com.gongyeon.io.netkim.model.dto.Pleaser;
import com.gongyeon.io.netkim.model.dto.Reporter;
import com.gongyeon.io.netkim.model.dto.Upgrader;
import com.gongyeon.io.netkim.model.entity.MemberEntity;
import com.gongyeon.io.netkim.model.entity.ReporterEntity;
import com.gongyeon.io.netkim.model.repository.MemberRepository;
import com.gongyeon.io.netkim.model.repository.ReporterRepository;
import com.gongyeon.io.netkim.model.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.BadRequestException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Tag(name = "관리자 페이지", description = "관리자로서 권한과 default 기자 명단을 관리하는 URL")
@RestController
@RequestMapping("/api-admin")
public class AdminController {
    private final MemberRepository memberRepository;
    private final ReporterRepository reporterRepository;
    private final AdminService adminService;

    public AdminController(MemberRepository memberRepository, ReporterRepository reporterRepository, AdminService adminService) {
        this.memberRepository = memberRepository;
        this.reporterRepository = reporterRepository;
        this.adminService = adminService;
    }

    @Operation(summary = "권한 부여 신청자 조회", description="권한 부여 신청자를 확인하기 위한 메서드")
    @GetMapping("")
    public ResponseEntity<List<Pleaser>> getAuthorizationList(){
        System.out.println("권한 부여 신청자 조회 : "+ LocalDate.now());
        List<MemberEntity> memberEntityList = memberRepository.getLevelUpMembers();
        if(memberEntityList==null || memberEntityList.isEmpty()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<Pleaser> memberList = new ArrayList<>();
        for(MemberEntity memberEntity : memberEntityList){
            memberList.add(new Pleaser(memberEntity));
        }
        return new ResponseEntity<>(memberList, HttpStatus.OK);
    }

    @Operation(summary = "권한 부여", description="권한 부여를 위한 메서드")
    @PostMapping("")
    public ResponseEntity<Void> setAuthorization(@RequestBody Upgrader member){
        adminService.upgrade(member);
        return ResponseEntity.ok().build();
    }

    @Operation(summary="Default 기자 명단 조회", description="Default 기자 명단 조회를 위한 메서드")
    @GetMapping("/default-pr")
    public ResponseEntity<List<ReporterEntity>> getDefaultReporter(){
        List<ReporterEntity> reporterList = reporterRepository.findAllByMemberIdx(0);
        if(reporterList==null || reporterList.isEmpty()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(reporterList, HttpStatus.OK);
    }

    @Operation(summary = "Default 기자 명단 추가", description = "Default 기자 명단 추가를 위한 메서드")
    @PostMapping("/default-pr")
    public ResponseEntity<Void> setDefaultReporter(@RequestBody Reporter reporter){
        try {
            adminService.addDReporter(reporter);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().build();
    }

    @Operation(description = "Default 기자 명단 추가를 위한 메서드")
    @PutMapping("/default-pr/{reporterId}")
    public ResponseEntity<Long> updateDefaultReporter(@PathVariable("reporterId") long reporterId, @RequestBody Reporter reporter){
        long result = adminService.updateDReporter(reporterId, reporter);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(description = "Default 기자 명단 삭제를 위한 메서드")
    @DeleteMapping("/default-pr/{reporterId}")
    public ResponseEntity<Void> deleteDefaultReporter(@PathVariable("reporterId") long reporterId){
        adminService.deleteDReporter(reporterId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "사용자 등록증 사진 조회")
    @GetMapping("/{certificateImg}")
    public ResponseEntity<?> getCertificateImg(@PathVariable("certificateImg") String certificateImg){
        File file = new File("data/certificates/"+certificateImg);
        Resource imgResource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + certificateImg + "\"")
                .body(imgResource);
    }
}
