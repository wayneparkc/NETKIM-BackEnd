package com.gongyeon.io.netkim.model.service;

public interface PressReleaseService {
    void makeRelease();
    void getReleaseFile();
    boolean sendReleaseFile(String prfnm);
}
