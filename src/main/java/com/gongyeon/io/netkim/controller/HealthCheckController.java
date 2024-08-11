package com.gongyeon.io.netkim.controller;

import com.gongyeon.io.netkim.model.entity.HealthTestEntity;
import com.gongyeon.io.netkim.model.repository.HealthTestRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping({"/", "/api-health"})
@Tag(name="상태 점검 Class", description = "Blue, Green 배포 등 상태 점검을 위한 Controller")
public class HealthCheckController {
    HealthTestRepository healthTestRepository;

    @Autowired
    public HealthCheckController(HealthTestRepository healthTestRepository) {
        this.healthTestRepository = healthTestRepository;
    }

    @GetMapping("")
    public ResponseEntity<String> healthCheck() {
        return new ResponseEntity<>("Connection Success!", HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<String> checkPost(@RequestBody String msg) {
        healthTestRepository.save(HealthTestEntity.builder().text(msg).build());
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }
}