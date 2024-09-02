package com.gongyeon.io.netkim.controller;

import com.gongyeon.io.netkim.model.dto.PressRelease;
import com.gongyeon.io.netkim.model.entity.PressReleaseEntity;
import com.gongyeon.io.netkim.model.service.PressReleaseService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
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
    public ResponseEntity<PressReleaseEntity> preview(@RequestBody PressRelease pressRelease){
        PressReleaseEntity pr = pressReleaseService.previewRelease(pressRelease);
        return new ResponseEntity<>(pr, HttpStatus.OK);
    }

    @Operation(summary="보도자료 저장하기", description="보도자료 파일을 작성하고, 저장을 시킨 후에 완성된 보도자료 id 넘기기")
    @PostMapping("")
    public ResponseEntity<PressReleaseEntity> writeRelease(@RequestHeader HttpHeaders headers, @RequestBody PressRelease pressRelease) throws Exception {
        PressReleaseEntity pressReleaseEntity = pressReleaseService.makeRelease(headers, pressRelease);
        return new ResponseEntity<>(pressReleaseEntity, HttpStatus.OK);
    }


    @PostMapping("/file")
    @Operation(summary="메일 전송", description = "파일 메일 전송 메서드")
    public ResponseEntity<Integer> sendFile(@RequestHeader HttpHeaders headers, @RequestBody Map<String, Long> map) throws MessagingException {
        long pressReleaseId = map.get("pressReleaseId");
        int result = pressReleaseService.sendReleaseFile(headers, pressReleaseId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}