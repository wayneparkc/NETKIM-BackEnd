package com.gongyeon.io.netkim.controller;

import com.gongyeon.io.netkim.model.dto.Reporter;
import com.gongyeon.io.netkim.model.entity.ReporterEntity;
import com.gongyeon.io.netkim.model.repository.ReporterRepository;
import com.gongyeon.io.netkim.model.service.ReporterService;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api-reporter")
public class ReporterController {
    private final ReporterRepository reporterRepository;
    private final ReporterService reporterService;

    public ReporterController(ReporterRepository reporterRepository, ReporterService reporterService) {
        this.reporterRepository = reporterRepository;
        this.reporterService = reporterService;
    }

    @Operation(summary = "기자 명단 조회", description = "사용자에게 등록된 기자 명단 조회를 위한 메서드")
    @GetMapping("")
    public ResponseEntity<List<ReporterEntity>> getAllReporters(@RequestHeader HttpHeaders headers) {
        try{
            return new ResponseEntity<>(reporterService.selectAllReporter(headers), HttpStatus.OK);
        }catch(BadRequestException e) {
            System.out.println("기자 명단에 조회자 없음");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary="reporterId로 상세조회", description="기자 정보 상세 확인을 위한 메서드 -> 등록자와 무관하게 조회 가능")
    @GetMapping("/{reporterId}")
    public ResponseEntity<ReporterEntity> getReporter(@PathVariable("reporterId") long reporterId){
        ReporterEntity reporter = reporterRepository.findByReporterId(reporterId);
        if(reporter ==null || reporter.getEmail()==null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(reporter, HttpStatus.OK);
    }

    @Operation(summary = "사용자 기자 추가", description="사용자가 본인의 기자를 등록")
    @PostMapping("")
    public ResponseEntity<Void> addReporter(@RequestHeader HttpHeaders headers, @RequestBody Reporter reporter) {
        try {
            reporterService.addReporter(headers, reporter);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary="기자 정보 수정", description = "MemberIdx에 맞는 기자 정보 수정을 위한 메서드")
    @PutMapping("/{reporterId}")
    public ResponseEntity<Long> updateReporter(@RequestHeader HttpHeaders headers, @PathVariable("reporterId") long reporterId, @RequestBody Reporter reporter){
        try {
            long result = reporterService.updateReporter(headers, reporterId, reporter);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary="기자 정보 삭제", description = "등록된 기자 명단 삭제를 위한 메서드")
    @DeleteMapping("/{reporterId}")
    public ResponseEntity<Void> deleteReporter(@RequestHeader HttpHeaders headers, @PathVariable("reporterId") long reporterId) {
        reporterService.removeReporter(headers, reporterId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
