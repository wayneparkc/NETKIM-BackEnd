package com.gongyeon.io.netkim.model.service;

import com.gongyeon.io.netkim.model.dto.Reporter;
import com.gongyeon.io.netkim.model.entity.ReporterEntity;
import com.gongyeon.io.netkim.model.jwt.JwtUtil;
import com.gongyeon.io.netkim.model.repository.ReporterRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReporterServiceImpl implements ReporterService {
    private final ReporterRepository reporterRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void addReporter(HttpHeaders headers, Reporter reporter) throws BadRequestException {
        long memberIdx = jwtUtil.getMemberIdx(headers.getFirst("Authorization").split(" ")[1]);
        // 중복검사
        if(reporterRepository.findByEmailAndMemberIdx(reporter.getEmail(), memberIdx)!=null){
            throw new BadRequestException("이미 존재하는 기자 정보 추가 시도");
        }

        ReporterEntity reporterEntity = ReporterEntity.builder()
                .email(reporter.getEmail())
                .reporterName(reporter.getReporterName())
                .press(reporter.getPress())
                .reporterType(reporter.getRType())
                .memberIdx(memberIdx)
                .build();
        reporterRepository.save(reporterEntity);
    }

    @Override
    @Transactional
    public long updateReporter(HttpHeaders headers, long reporterId, Reporter reporter) throws BadRequestException {
        long memberIdx = jwtUtil.getMemberIdx(headers.getFirst("Authorization").split(" ")[1]);
        ReporterEntity reporterEntity = reporterRepository.findByReporterId(reporterId);
        if(reporterEntity==null || reporterEntity.getMemberIdx()!=memberIdx){
            throw new BadRequestException();
        }
        reporterEntity.setReporterName(reporter.getReporterName());
        reporterEntity.setPress(reporter.getPress());
        reporterEntity.setEmail(reporter.getEmail());
        reporterEntity.setReporterType(reporter.getRType());
        reporterRepository.save(reporterEntity);
        return reporterEntity.getReporterId();
    }

    @Override
    @Transactional
    public void removeReporter(HttpHeaders headers, long reporterId){
        long memberIdx = jwtUtil.getMemberIdx(headers.getFirst("Authorization").split(" ")[1]);
        reporterRepository.deleteByReporterIdAndMemberIdx(reporterId, memberIdx);
    }
}
