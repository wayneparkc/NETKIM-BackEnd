package com.gongyeon.io.netkim.model.service;

import com.gongyeon.io.netkim.model.dto.PressRelease;
import com.gongyeon.io.netkim.model.entity.MemberEntity;
import com.gongyeon.io.netkim.model.entity.PerformanceEntity;
import com.gongyeon.io.netkim.model.entity.PressReleaseEntity;
import com.gongyeon.io.netkim.model.entity.ReporterEntity;
import com.gongyeon.io.netkim.model.jwt.JwtUtil;
import com.gongyeon.io.netkim.model.repository.MemberRepository;
import com.gongyeon.io.netkim.model.repository.PerformanceRepository;
import com.gongyeon.io.netkim.model.repository.PressReleaseRepository;
import com.gongyeon.io.netkim.model.repository.ReporterRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kr.dogfoot.hwplib.object.HWPFile;
import kr.dogfoot.hwplib.object.bodytext.Section;
import kr.dogfoot.hwplib.object.bodytext.control.Control;
import kr.dogfoot.hwplib.object.bodytext.control.ControlTable;
import kr.dogfoot.hwplib.object.bodytext.control.ControlType;
import kr.dogfoot.hwplib.object.bodytext.control.table.Cell;
import kr.dogfoot.hwplib.object.bodytext.control.table.Row;
import kr.dogfoot.hwplib.object.bodytext.paragraph.Paragraph;
import kr.dogfoot.hwplib.reader.HWPReader;
import kr.dogfoot.hwplib.tool.objectfinder.ControlFilter;
import kr.dogfoot.hwplib.tool.objectfinder.ControlFinder;
import kr.dogfoot.hwplib.writer.HWPWriter;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class PressReleaseServiceImpl implements PressReleaseService {
    private final PressReleaseRepository pressReleaseRepository;
    private final JavaMailSender mailSender;
    private final JwtUtil jwtUtil;
    private final PerformanceRepository performanceRepository;
    private final ReporterRepository reporterRepository;
    private final MemberRepository memberRepository;
    private final InsertingImage insertingImage = new InsertingImage();

    public PressReleaseServiceImpl(PressReleaseRepository pressReleaseRepository, JavaMailSender mailSender, JwtUtil jwtUtil, PerformanceRepository performanceRepository, ReporterRepository reporterRepository, MemberRepository memberRepository) {
        this.pressReleaseRepository = pressReleaseRepository;
        this.mailSender = mailSender;
        this.jwtUtil = jwtUtil;
        this.performanceRepository = performanceRepository;
        this.reporterRepository = reporterRepository;
        this.memberRepository = memberRepository;
    }
    
    @Override
    @Operation(summary = "작성한 보도자료 조회 메서드")
    @Transactional
    public List<PressReleaseEntity> getAllPressRelease(HttpHeaders headers) throws NotFoundException {
        long memberIdx = jwtUtil.getMemberIdx(headers.getFirst("Authorization").split(" ")[1]);
        List<PressReleaseEntity> pressReleaseList = pressReleaseRepository.findByMemberIdxOrderByPressReleaseIdDesc(memberIdx);
        if(pressReleaseList==null || pressReleaseList.isEmpty()){
            throw new NotFoundException();
        }
        return pressReleaseList;
    }

    @Override
    public PressReleaseEntity getDetailPressRelease(HttpHeaders headers, long pressReleaseId) throws NotFoundException {
        long memberIdx = jwtUtil.getMemberIdx(headers.getFirst("Authorization").split(" ")[1]);
        PressReleaseEntity pressRelease = pressReleaseRepository.findByPressReleaseId(pressReleaseId);
        if(memberIdx == pressRelease.getMemberIdx())
            return pressRelease;
        throw new NotFoundException();
    }

    @Operation(summary = "미리보기 제작 메서드", description = "별도로 저장은 하지 않고, 가지고 있는 정보를 가지고 임시본을 제작한다.")
    @Override
    public PressReleaseEntity previewRelease(PressRelease pressRelease) {
        PerformanceEntity performance = performanceRepository.findByPrfid(pressRelease.getPerformanceId());
        return PressReleaseEntity.builder()
                .headLine(makeHeadLine(performance, pressRelease))
                .content(makeBody(performance, pressRelease))
                .performance(performance)
                .build();
    }

    // 보도자료 파일을 제작하고, 저장하는 메서드
    @Transactional
    @Override
    public PressReleaseEntity makeRelease(HttpHeaders headers, PressRelease pressRelease) throws Exception {
        long memberIdx = jwtUtil.getMemberIdx(headers.getFirst("Authorization").split(" ")[1]);
        MemberEntity member = memberRepository.findByMemberIdx(memberIdx);
        long prfId = pressRelease.getPerformanceId();
//        String filepath = "C://Users/SSAFY/Desktop/";
        String filepath = "data/hwp/";
        PerformanceEntity performance = performanceRepository.findByPrfid(prfId);
        String filename = performance.getKopisId() + "_pr.hwp";

        // 내용 불러오기
        HWPFile file = HWPReader.fromFile(filepath+"NewForm.hwp");
        ArrayList<Control> result = ControlFinder.find(file, new ControlFilter(){
            // 한 파일 내에서 Table의 속성을 가진 모든 객체를 찾기 위하여, 테이블 속성을 가졌는지 확인하는 메서드
            @Override
            public boolean isMatched(Control control, Paragraph paragraph, Section section) {
                return control.getType() == ControlType.Table;
            }
        });

        if(result != null && !result.isEmpty()) {
            // 보도자료 작성 양식의 본문 찾기
            Control control = result.get(0);
            ControlTable table = (ControlTable) control;

            Row releaseDateRow = table.getRowList().get(1);
            releaseDateRow.getCellList().get(2).getParagraphList().getParagraph(0).getText().addString("즉시 사용 가능합니다.");
            releaseDateRow.getCellList().get(4).getParagraphList().getParagraph(0).getText().addString(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd. hh:mm")));

            Cell headCell = table.getRowList().get(3).getCellList().get(1);
            headCell.getParagraphList().getParagraph(0).getText().addString(makeHeadLine(performance, pressRelease));

            Cell bodyCell = table.getRowList().get(4).getCellList().get(0);
            bodyCell.getParagraphList().getParagraph(0).getText().addString(makeBody(performance, pressRelease));

            if(member.getCompany() != null) {
                table.getRowList().get(5).getCellList().get(1).getParagraphList().getParagraph(0).getText().addString(member.getCompany());
            }
            table.getRowList().get(5).getCellList().get(3).getParagraphList().getParagraph(0).getText().addString(member.getMemberName());
            table.getRowList().get(5).getCellList().get(4).getParagraphList().getParagraph(0).getText().addString(member.getPhone());

            insertingImage.insertShapeWithImage(performance.getPoster(), file);

            HWPWriter.toFile(file, filepath + filename);
        }
        PressReleaseEntity pressReleaseEntity = PressReleaseEntity.builder()
                    .performance(performance)
                    .memberIdx(memberIdx)
                    .headLine(makeHeadLine(performance, pressRelease))
                    .content(makeBody(performance, pressRelease))
                    .filename("https://gongyeon.kro.kr/api-file/press/"+filename)
                    .build();
        pressReleaseRepository.save(pressReleaseEntity);
        return pressReleaseEntity;
    }

    // 저장된 파일 조회 메서드
//    public String getReleaseFile() throws Exception {
//        HWPFile file = HWPReader.fromFile("data/hwp/NewForm.hwp");
//        ArrayList<Control> result = ControlFinder.find(file, new ControlFilter(){
//            // 한 파일 내에서 Table의 속성을 가진 모든 객체를 찾기 위하여, 테이블 속성을 가졌는지 확인하는 메서드
//            @Override
//            public boolean isMatched(Control control, Paragraph paragraph, Section section) {
//                return control.getType() == ControlType.Table;
//            }
//        });
//
//        if(result != null && !result.isEmpty()) {
//            Control control = result.get(0);
//            ControlTable table = (ControlTable) control;
//
//            for(Row row : table.getRowList()) {
//                for(Cell cell : row.getCellList()) {
//                    System.out.print(cell.getParagraphList().getNormalString() + " | ");
//                }
//                System.out.println();
//            }
//        }
//        return file.getBodyText().toString();
//    }

    @Override
    @Transactional
    public int sendReleaseFile(HttpHeaders headers, long pressReleaseId) throws MessagingException {
        int result=0;
        long memberIdx = jwtUtil.getMemberIdx(headers.getFirst("Authorization").split(" ")[1]);
        MemberEntity memberEntity = memberRepository.findByMemberIdx(memberIdx);
        PressReleaseEntity pressRelease = pressReleaseRepository.findByPressReleaseId(pressReleaseId);
        PerformanceEntity performance = pressRelease.getPerformance();

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        String msg = "안녕하세요? " + memberEntity.getCompany()+" "+memberEntity.getMemberName()+"입니다."
                + "\n"+pressRelease.getPerformance().getPrfnm()+"관련 보도자료 보내드립니다."
                + "\n확인해주시고, 멋진 기사 부탁드립니다!"
                + "\n\n담당자 : "+memberEntity.getMemberName()+" "+memberEntity.getEmail()+memberEntity.getPhone();
        MimeMessageHelper helper;

        List<ReporterEntity> ReporterList = reporterRepository.findAllByMemberIdx(memberIdx);
        if(ReporterList != null && !ReporterList.isEmpty()) {
            for(ReporterEntity reporterEntity : ReporterList) {
                helper = new MimeMessageHelper(mimeMessage, true);
                helper.setFrom(memberEntity.getEmail());
                helper.setTo(reporterEntity.getEmail());
                helper.setSubject(pressRelease.getHeadLine());
                helper.setText(msg);
                helper.addAttachment(performance.getPrfnm()+" 보도자료", new File("data/hwp/"+performance.getKopisId() + "_pr.hwp"));
                mailSender.send(mimeMessage);
                result++;
            }
        }
        return result;
    }

    private String makeHeadLine(PerformanceEntity performance, PressRelease pressRelease) {
        String title = performance.getPrfnm().replaceAll("\\[.*?\\]|\\(.*?\\)", "").trim();
        StringBuilder sb = new StringBuilder();
        if(pressRelease.getKey()!=null || !pressRelease.getKey().equals("")) {
            sb.append(pressRelease.getKey()).append(", ");
        }
        sb.append("뮤지컬 '").append(title).append("'").append(" ").append(performance.getPrfdfrom().getMonthValue()).append("월 ").append(performance.getPrfdfrom().getDayOfMonth()).append("일 개막");
        return sb.toString();
    }

    private String makeBody(PerformanceEntity performance, PressRelease pressRelease) {
        String title = performance.getPrfnm().replaceAll("\\[.*?\\]|\\(.*?\\)", "").trim();
        LocalDate stDate = performance.getPrfdfrom();
        LocalDate endDate = performance.getPrfdto();
        String synopsis = pressRelease.getSynopsis();
        String cast = pressRelease.getActors();

        StringBuilder content = new StringBuilder("○ 뮤지컬 '"+ title +"'"+ getParticle(title, false)+" 오는 "+stDate.getMonthValue()+"월 "+stDate.getDayOfMonth()+"일 개막한다.");
        if(synopsis != null && !synopsis.isEmpty()) {
            content.append("\n\n○ 뮤지컬 '").append(title).append("'").append(getParticle(title, true)).append(" ").append(synopsis).append("는 이야기로 ").append(performance.getPrfruntime()).append("분간 관객에게 감동을 선사한다.");
        }
        if(cast != null && !cast.isEmpty()) {
            content.append("\n\n").append("○ 이번 뮤지컬 '").append(title).append("'에서는 ").append(cast);
            if(cast.charAt(cast.length()-1)=='등') {
                content.append("이 출연한다.");
            }else{
                content.append("등이 출연한다.");
            }
        }else{
            content.append("\n\n").append("○ 이번 뮤지컬 '").append(title).append("'에서는 ").append(performance.getPrfcast()).append("이 출연한다.");
        }
        if(pressRelease.getInterviewee() != null && !pressRelease.getInterviewee().isEmpty()) {
            content.append("\n\n").append("○ 이번 뮤지컬에 참여한 '").append(pressRelease.getInterviewee()).append("'는 '").append(getParticle(pressRelease.getInterviewee(), true)).append(pressRelease.getInterviewContent()).append("'라며 소감을 남겼다.");
        }
        content.append("\n\n").append("○ 한편, 이번 뮤지컬 '").append(title).append("'").append(getParticle(title, true));
        if(pressRelease.getSeats() > 1000){
            content.append(pressRelease.getSeats()).append(" 이상의 관객이 찾아와 관람하였으며,");
        }
        content.append(" 오는 ").append(stDate.getMonthValue()).append("월 ").append(stDate.getDayOfMonth()).append("일 부터 ").append(endDate.getMonthValue()).append("월 ").append(endDate.getDayOfMonth()).append("일 까지 ").append(performance.getFcltynm()).append("에서 공연된다. //끝//");;

        return content.toString();
    }

    // 조사를 결정하여 반환하는 메서드
    private String getParticle(String word, boolean isSubject) {
        if (word == null || word.isEmpty()) {
            return "";
        }

        char lastChar = word.charAt(word.length() - 1);

        if (isHangul(lastChar)) {
            if (hasFinalConsonant(lastChar)) {
                if(isSubject) {
                    return "은";
                }
                return "이";
            } else {
                if(isSubject) {
                    return "는";
                }
                return "가";
            }
        }

        return ""; // 한글이 아닌 경우 빈 문자열 반환
    }

    // UTF-8 에 따라, 한글인지 판별하는 메서드
    private boolean isHangul(char c) {
        return c >= 0xAC00 && c <= 0xD7A3;
    }

    // 종성을 가지고 있는지 확인하는 메서드
    private boolean hasFinalConsonant(char c) {
        int base = 0xAC00;
        int finalConsonantCount = 28;
        int index = c - base;
        return (index % finalConsonantCount) != 0;
    }
}