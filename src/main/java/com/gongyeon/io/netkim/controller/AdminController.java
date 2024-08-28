package com.gongyeon.io.netkim.controller;

import com.gongyeon.io.netkim.model.dto.Reporter;
import com.gongyeon.io.netkim.model.dto.Upgrader;
import com.gongyeon.io.netkim.model.entity.MemberEntity;
import com.gongyeon.io.netkim.model.entity.ReporterEntity;
import com.gongyeon.io.netkim.model.entity.Role;
import com.gongyeon.io.netkim.model.repository.MemberRepository;
import com.gongyeon.io.netkim.model.repository.ReporterRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api-admin")
public class AdminController {
    private final MemberRepository memberRepository;
    private final ReporterRepository reporterRepository;

    public AdminController(MemberRepository memberRepository, ReporterRepository reporterRepository) {
        this.memberRepository = memberRepository;
        this.reporterRepository = reporterRepository;
    }

    @Operation(description="권한 부여 신청자를 확인하기 위한 메서드")
    @GetMapping("")
    public ResponseEntity<List<MemberEntity>> getAuthorizationList(){
        List<MemberEntity> memberList = memberRepository.getLevelUpMembers();
        if(memberList==null || memberList.isEmpty()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(memberList, HttpStatus.OK);
    }

    @Operation(description="권한 부여를 위한 메서드")
    @PostMapping("")
    public ResponseEntity<Void> setAuthorization(@RequestBody Upgrader member){
        // 회사명을 던져줘
        MemberEntity manager = memberRepository.findByMemberIdx(member.getMemberIdx());
        manager.setCompany(member.getCompanyName());
        manager.setRole(Role.MANAGER);
        memberRepository.save(manager);
        // 등급 업 시키는 순간에 default 기자 목록 추가하기
        List<ReporterEntity> defaultReporterList = reporterRepository.findAllByMemberIdx(0);
        for(ReporterEntity reporter : defaultReporterList){
            reporter.setMemberIdx(member.getMemberIdx());
            reporterRepository.save(reporter);
        }
        return ResponseEntity.ok().build();
    }

    @Operation(description="Default 기자 명단 조회를 위한 메서드")
    @GetMapping("/default-pr")
    public ResponseEntity<List<ReporterEntity>> getDefaultReporter(){
        List<ReporterEntity> reporterList = reporterRepository.findAllByMemberIdx(0);
        if(reporterList==null || reporterList.isEmpty()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(reporterList, HttpStatus.OK);
    }

    @Operation(description="Default 기자 정보 상세 확인을 위한 메서드")
    @GetMapping("/default-pr/{reporterId}")
    public ResponseEntity<ReporterEntity> getDefaultReporter(@PathVariable("reporterId") long reporterId){
        ReporterEntity reporter = reporterRepository.findByReporterId(reporterId);
        if(reporter ==null || reporter.getEmail()==null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(reporter, HttpStatus.OK);
    }

    @Operation(description = "Default 기자 명단 추가를 위한 메서드")
    @PostMapping("/default-pr")
    public ResponseEntity<Void> setDefaultReporter(@RequestBody Reporter reporter){
        // 중복검사
        if(reporterRepository.findByEmailAndMemberIdx(reporter.getEmail(), 0)==null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        ReporterEntity reporterEntity = ReporterEntity.builder()
                .email(reporter.getEmail())
                .reporterName(reporter.getReporterName())
                .press(reporter.getPress())
                .reporterType(reporter.getRType())
                .memberIdx(0)
                .build();

        reporterRepository.save(reporterEntity);
        return ResponseEntity.ok().build();
    }

    @Operation(description = "Default 기자 명단 추가를 위한 메서드")
    @PutMapping("/default-pr/{reporterId}")
    public ResponseEntity<Long> updateDefaultReporter(@PathVariable("reporterId") long reporterId, @RequestBody Reporter reporter){
        ReporterEntity reporterEntity = reporterRepository.findByReporterId(reporterId);
        reporterEntity.setReporterName(reporter.getReporterName());
        reporterEntity.setPress(reporter.getPress());
        reporterEntity.setEmail(reporter.getEmail());
        reporterEntity.setReporterType(reporter.getRType());
        reporterRepository.save(reporterEntity);
        return new ResponseEntity<>(reporterEntity.getReporterId(), HttpStatus.OK);
    }

    @Operation(description = "Default 기자 명단 삭제를 위한 메서드")
    @DeleteMapping("/default-pr/{reporterId}")
    public ResponseEntity<Void> deleteDefaultReporter(@PathVariable("reporterId") long reporterId){
        reporterRepository.deleteByReporterIdAndMemberIdx(reporterId, 0);
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
