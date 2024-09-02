package com.gongyeon.io.netkim.model.service;

import com.gongyeon.io.netkim.model.dto.Reporter;
import com.gongyeon.io.netkim.model.dto.Upgrader;
import org.apache.coyote.BadRequestException;

public interface AdminService {
    void upgrade(Upgrader member);
    void addDReporter(Reporter reporter) throws BadRequestException;
    long updateDReporter(long reporterId, Reporter reporter);
    void deleteDReporter(long reporterId);
}
