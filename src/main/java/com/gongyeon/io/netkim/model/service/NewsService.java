package com.gongyeon.io.netkim.model.service;


public interface NewsService {
    String read(String str) throws Exception;
    void write(String locate, String prfId);
}
