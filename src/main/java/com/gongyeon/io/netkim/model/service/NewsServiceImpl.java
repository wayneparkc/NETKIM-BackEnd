package com.gongyeon.io.netkim.model.service;

import kr.dogfoot.hwplib.object.HWPFile;
import kr.dogfoot.hwplib.object.bodytext.paragraph.Paragraph;
import kr.dogfoot.hwplib.object.bodytext.paragraph.text.ParaText;
import kr.dogfoot.hwplib.reader.HWPReader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;


@Service
public class NewsServiceImpl implements NewsService {
    private static ResourceLoader resourceLoader;

    public NewsServiceImpl(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public String read(String fileName) throws Exception {
        Resource resource = resourceLoader.getResource("classpath:static/" + fileName+ ".hwp");
        HWPFile file = HWPReader.fromFile(resource.getFile());
        System.out.println(file.getBodyText().getLastSection().getLastParagraph().getText());
        return file.getBodyText().toString();
    }

    private void setParaText(Paragraph p, String text2) {
        p.createText();
        ParaText pt = p.getText();
        try {
            pt.addString(text2);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(String locate, String prfId) {

    }
}
