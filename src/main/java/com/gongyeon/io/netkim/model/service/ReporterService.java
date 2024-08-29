package com.gongyeon.io.netkim.model.service;

import com.gongyeon.io.netkim.model.dto.Reporter;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpHeaders;

public interface ReporterService {
    void removeReporter(HttpHeaders headers, long reporterId);
    void addReporter(HttpHeaders headers, Reporter reporter) throws BadRequestException;
    long updateReporter(HttpHeaders headers, long reporterId, Reporter reporter) throws BadRequestException;
}
