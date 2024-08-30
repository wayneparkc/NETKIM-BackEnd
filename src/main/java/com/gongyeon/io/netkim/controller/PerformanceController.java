package com.gongyeon.io.netkim.controller;

import com.gongyeon.io.netkim.model.entity.PerformanceEntity;
import com.gongyeon.io.netkim.model.service.PerformanceService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api-prf")
public class PerformanceController {
    private final PerformanceService performanceService;
    public PerformanceController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }
    
    @Operation(summary="공연 제목 전체 조회")
    @GetMapping("")
    public ResponseEntity<List<String>> getAllprfnm() {
        List<String> performanceList = performanceService.getAllprfnm();
        if(performanceList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(performanceList, HttpStatus.OK);
    }

    // 저장된 공연 전체 조회 메서드
    @GetMapping("/all-prf")
    public ResponseEntity<List<PerformanceEntity>> getAllPerformance() {
        List<PerformanceEntity> performanceList = performanceService.getAllPerformance();
        if(performanceList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(performanceList, HttpStatus.OK);
    }
    
    // 공연 정보 상세 조회 메서드
    @Operation(summary = "공연 id로 공연 조회")
    @GetMapping("/{prfId}")
    public ResponseEntity<PerformanceEntity> getPerformanceById(@PathVariable("prfId") String prfId) {
        try{
            PerformanceEntity performance = performanceService.getDetail(prfId);
            return new ResponseEntity<>(performance, HttpStatus.OK);
        }catch(NullPointerException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // 공연 이름으로 정보 찾는 조회 메서드
    @Operation(summary="공연 제목 조회")
    @PostMapping("/find")
    public ResponseEntity<PerformanceEntity> getPerformanceByName(@RequestBody Map<String, String> map) {
        System.out.println(map);
        try{
            PerformanceEntity performance = performanceService.getDetailName(map.get("prfnm"));
            return new ResponseEntity<>(performance, HttpStatus.OK);
        }catch(NullPointerException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    // KOPIS로 부터 공연 정보 추가 메서드 (초기세팅용)
    @PostMapping("")
    public ResponseEntity<Integer> addPerformance() {
        return new ResponseEntity<>(performanceService.insertPerformance(), HttpStatus.CREATED);
    }

    @PutMapping("{kopisId}")
    public ResponseEntity<PerformanceEntity> updatePerformance(@PathVariable("kopisId") String kopisId) {
        return new ResponseEntity<>(performanceService.updatePerformance(kopisId), HttpStatus.OK);
    }
}
