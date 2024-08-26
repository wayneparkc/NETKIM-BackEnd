package com.gongyeon.io.netkim.model.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class PressReleaseServiceImpl implements PressReleaseService {
    private final JavaMailSender mailSender;

    public PressReleaseServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void makeRelease() {

    }

    @Override
    public void getReleaseFile() {

    }

    @Override
    public boolean sendReleaseFile(String prfnm) {
        boolean msg = false;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("[공연이요] "+prfnm+" 보도자료입니다.");
        message.setFrom("gongyeon@gmail.com");
        message.setTo("gongyeon@gmail.com");

        return msg;
    }
}
