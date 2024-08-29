package com.gongyeon.io.netkim.controller;

import com.gongyeon.io.netkim.model.dto.PressRelease;
import com.gongyeon.io.netkim.model.entity.PressReleaseEntity;
import com.gongyeon.io.netkim.model.repository.PressReleaseRepository;
import com.gongyeon.io.netkim.model.service.PressReleaseService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api-news")
public class PressReleaseController {
    private final PressReleaseService pressReleaseService;
    private final PressReleaseRepository pressReleaseRepository;

    public PressReleaseController(PressReleaseService pressReleaseService, PressReleaseRepository pressReleaseRepository) {
        this.pressReleaseService = pressReleaseService;
        this.pressReleaseRepository = pressReleaseRepository;
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

    @Operation(summary="보도자료 저장하기")
    @PostMapping("")
    public ResponseEntity<PressReleaseEntity> writeRelease(@RequestHeader HttpHeaders headers, @RequestBody PressRelease pressRelease) throws Exception {
        PressReleaseEntity pressReleaseEntity = pressReleaseService.makeRelease(headers, pressRelease);
        return new ResponseEntity<>(pressReleaseEntity, HttpStatus.OK);
    }

    @GetMapping("/file/{pressReleaseId}")
    public ResponseEntity<Resource> getFile(@PathVariable("pressReleaseId") long pressReleaseId) throws Exception {

        PressReleaseEntity file = pressReleaseRepository.findByPressReleaseId(pressReleaseId);

        String filename = file.getFilename();
        String filePath = "data/hwp/" + filename;

        Resource resource = new UrlResource(Paths.get(filePath).toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + UriUtils.encode(filename, "UTF-8") + "\"")
                .body(resource);
    }

    @PostMapping("/file")
    @Operation(description = "파일 메일 전송 메서드")
    public ResponseEntity<Integer> sendFile(@RequestHeader HttpHeaders headers, @RequestBody Map<String, Long> map) throws MessagingException {
        long pressReleaseId = map.get("pressReleaseId");
        int result = pressReleaseService.sendReleaseFile(headers, pressReleaseId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}