package com.gongyeon.io.netkim.model.service;

import com.gongyeon.io.netkim.model.dto.Reporter;
import com.gongyeon.io.netkim.model.dto.Upgrader;
import com.gongyeon.io.netkim.model.entity.MemberEntity;
import com.gongyeon.io.netkim.model.entity.ReporterEntity;
import com.gongyeon.io.netkim.model.entity.Role;
import com.gongyeon.io.netkim.model.repository.MemberRepository;
import com.gongyeon.io.netkim.model.repository.ReporterRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final ReporterRepository reporterRepository;
    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public void upgrade(Upgrader member) {
        MemberEntity manager = memberRepository.findByMemberIdx(member.getMemberIdx());
        manager.setCompany(member.getCompanyName());
        manager.setRole(Role.MANAGER);
        memberRepository.save(manager);
        // 등급 업 시키는 순간에 default 기자 목록 추가하기
//        List<ReporterEntity> defaultReporterList = reporterRepository.findAllByMemberIdx(0);
//        for (ReporterEntity reporter : defaultReporterList) {
//            ReporterEntity newReporter = ReporterEntity.builder()
//                    .reporterName(reporter.getReporterName())
//                    .reporterType(reporter.getReporterType())
//                    .email(reporter.getEmail())
//                    .press(reporter.getPress())
//                    .memberIdx(member.getMemberIdx())
//                    .build();
//            reporterRepository.save(newReporter);
//        }
    }

    @Transactional
    @Override
    public void addDReporter(Reporter reporter) throws BadRequestException {
        if(reporterRepository.findByEmailAndMemberIdx(reporter.getEmail(), 0)!=null){
            throw new BadRequestException("이미 존재하는 이메일 입니다.");
        }

        ReporterEntity reporterEntity = ReporterEntity.builder()
                .email(reporter.getEmail())
                .reporterName(reporter.getReporterName())
                .press(reporter.getPress())
                .reporterType(reporter.getRType())
                .memberIdx(0)
                .build();

        reporterRepository.save(reporterEntity);
    }

    @Override
    @Transactional
    public long updateDReporter(long reporterId, Reporter reporter) {
        ReporterEntity reporterEntity = reporterRepository.findByReporterId(reporterId);
        reporterEntity.setReporterName(reporter.getReporterName());
        reporterEntity.setPress(reporter.getPress());
        reporterEntity.setEmail(reporter.getEmail());
        reporterEntity.setReporterType(reporter.getRType());
        reporterRepository.save(reporterEntity);
        return reporterId;
    }

    @Override
    @Transactional
    public void deleteDReporter(long reporterId) {
        reporterRepository.deleteByReporterIdAndMemberIdx(reporterId, 0);
    }
}
