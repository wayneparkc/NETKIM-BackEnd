package com.gongyeon.io.netkim.model.service;

import com.gongyeon.io.netkim.model.dto.PressRelease;
import com.gongyeon.io.netkim.model.entity.PressReleaseEntity;
import jakarta.mail.MessagingException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpHeaders;

import java.util.List;

public interface PressReleaseService {
    List<PressReleaseEntity> getAllPressRelease(HttpHeaders headers) throws ChangeSetPersister.NotFoundException;
    PressReleaseEntity getDetailPressRelease(HttpHeaders headers, long pressReleaseId) throws ChangeSetPersister.NotFoundException;
    PressReleaseEntity previewRelease(PressRelease pressRelease);
    PressReleaseEntity makeRelease(HttpHeaders headers, PressRelease pressRelease);
    String getReleaseFile() throws Exception;
    int sendReleaseFile(HttpHeaders headers, long pressReleaseId) throws MessagingException;
}
