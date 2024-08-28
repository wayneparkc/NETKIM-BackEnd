package com.gongyeon.io.netkim.controller;

import com.gongyeon.io.netkim.model.dto.Reporter;
import com.gongyeon.io.netkim.model.entity.ReporterEntity;
import com.gongyeon.io.netkim.model.jwt.JwtUtil;
import com.gongyeon.io.netkim.model.repository.ReporterRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api-reporter")
public class ReporterController {
    private final JwtUtil jwtUtil;
    private final ReporterRepository reporterRepository;

    public ReporterController(JwtUtil jwtUtil, ReporterRepository reporterRepository) {
        this.jwtUtil = jwtUtil;
        this.reporterRepository = reporterRepository;
    }

    @Operation(description = "기자 명단 조회를 위한 메서드")
    @GetMapping("")
    public ResponseEntity<List<ReporterEntity>> getAllReporters(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String token) {
        long memberIdx = jwtUtil.getMemberIdx(token.split(" ")[1]);
        List<ReporterEntity> reporterList = reporterRepository.findAllByMemberIdx(memberIdx);
        if (reporterList == null || reporterList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(reporterList, HttpStatus.OK);
    }

    @Operation(description = "Default 기자 명단 추가를 위한 메서드")
    @PostMapping("")
    public ResponseEntity<Void> setDefaultReporter(@RequestHeader HttpHeaders headers, @RequestBody Reporter reporter){
        long memberIdx = jwtUtil.getMemberIdx(headers.getFirst("Authorization").split(" ")[1]);
        // 중복검사
        if(reporterRepository.findByEmailAndMemberIdx(reporter.getEmail(), memberIdx)!=null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        ReporterEntity reporterEntity = ReporterEntity.builder()
                .email(reporter.getEmail())
                .reporterName(reporter.getReporterName())
                .press(reporter.getPress())
                .memberIdx(memberIdx)
                .build();
        reporterRepository.save(reporterEntity);
        return ResponseEntity.ok().build();
    }

    @Operation(description = "MemberIdx에 맞는 기자 정보 수정을 위한 메서드")
    @PutMapping("/{reporterId}")
    public ResponseEntity<Long> updateDefaultReporter(@RequestHeader HttpHeaders headers, @PathVariable("reporterId") long reporterId, @RequestBody Reporter reporter){
        long memberIdx = jwtUtil.getMemberIdx(headers.getFirst("Authorization").split(" ")[1]);
        ReporterEntity reporterEntity = reporterRepository.findByReporterId(reporterId);
        if(reporterEntity==null || reporterEntity.getMemberIdx()!=memberIdx){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        reporterEntity.setReporterName(reporter.getReporterName());
        reporterEntity.setPress(reporter.getPress());
        reporterEntity.setEmail(reporter.getEmail());
        reporterRepository.save(reporterEntity);
        return new ResponseEntity<>(reporterEntity.getReporterId(), HttpStatus.OK);
    }

    @Operation(description = "Default 기자 명단 삭제를 위한 메서드")
    @DeleteMapping("/{reporterId}")
    public ResponseEntity<Void> deleteDefaultReporter(@RequestHeader HttpHeaders headers, @PathVariable("reporterId") long reporterId) {
        long memberIdx = jwtUtil.getMemberIdx(headers.getFirst("Authorization").split(" ")[1]);
        reporterRepository.deleteByReporterIdAndMemberIdx(reporterId, memberIdx);
        return ResponseEntity.ok().build();
    }
}
