package com.gongyeon.io.netkim.controller;

import com.gongyeon.io.netkim.model.dto.PressRelease;
import com.gongyeon.io.netkim.model.entity.PressReleaseEntity;
import com.gongyeon.io.netkim.model.service.PressReleaseService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api-news")
public class PressReleaseController {
    private final PressReleaseService pressReleaseService;

    @Autowired
    public PressReleaseController(PressReleaseService pressReleaseService) {
        this.pressReleaseService = pressReleaseService;
    }
    
    @Operation(summary="작성한 보도자료 전체 조회 메서드")
    @GetMapping("")
    public ResponseEntity<List<PressReleaseEntity>> read(@RequestHeader HttpHeaders headers) throws Exception {
        List<PressReleaseEntity> pressReleaseList = pressReleaseService.getAllPressRelease(headers);
        return new ResponseEntity<>(pressReleaseList, HttpStatus.OK);
    }

    @Operation(summary="작성한 보도자료 상세 조회 메서드")
    @GetMapping("/{pressReleaseId}")
    public ResponseEntity<PressReleaseEntity> read(@RequestHeader HttpHeaders headers, @PathVariable("pressReleaseId") long pressReleaseId) {
        try {
            PressReleaseEntity pressRelease = pressReleaseService.getDetailPressRelease(headers, pressReleaseId);
            return new ResponseEntity<>(pressRelease, HttpStatus.OK);
        }catch(Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary="보도자료 미리보기")
    @PostMapping("/preview")
    public PressReleaseEntity preview(@RequestBody PressRelease pressRelease){
        return pressReleaseService.previewRelease(pressRelease);
    }

    @Operation(summary="보도자료 저장하기")
    @PostMapping("")
    public ResponseEntity<?> writeRelease(@RequestHeader HttpHeaders headers, @RequestBody PressRelease pressRelease){
        pressReleaseService.makeRelease(headers, pressRelease);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/file")
    public ResponseEntity<String> getFile() throws Exception {
        // 오늘까지 다 끝내기!
        return new ResponseEntity<>(pressReleaseService.getReleaseFile(), HttpStatus.CREATED);
    }

    @PostMapping("/file")
    @Operation(description = "파일 메일 전송 메서드")
    public ResponseEntity<Integer> sendFile(@RequestHeader HttpHeaders headers, @RequestBody Map<String, Long> map) throws MessagingException {
        long pressReleaseId = map.get("pressReleaseId");
        int result = pressReleaseService.sendReleaseFile(headers, pressReleaseId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
