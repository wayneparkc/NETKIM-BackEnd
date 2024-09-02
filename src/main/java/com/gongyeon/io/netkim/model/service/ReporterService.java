package com.gongyeon.io.netkim.model.service;

import com.gongyeon.io.netkim.model.dto.Reporter;
import com.gongyeon.io.netkim.model.entity.ReporterEntity;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpHeaders;

import java.util.List;

public interface ReporterService {
    List<ReporterEntity> selectAllReporter(HttpHeaders headers) throws BadRequestException;
    void addReporter(HttpHeaders headers, Reporter reporter) throws BadRequestException;
    long updateReporter(HttpHeaders headers, long reporterId, Reporter reporter) throws BadRequestException;
    void removeReporter(HttpHeaders headers, long reporterId);
}
