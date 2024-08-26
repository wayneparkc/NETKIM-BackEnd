package com.gongyeon.io.netkim.model.service;

import kr.dogfoot.hwplib.object.HWPFile;
import kr.dogfoot.hwplib.object.bodytext.Section;
import kr.dogfoot.hwplib.object.bodytext.control.Control;
import kr.dogfoot.hwplib.object.bodytext.control.ControlTable;
import kr.dogfoot.hwplib.object.bodytext.control.ControlType;
import kr.dogfoot.hwplib.object.bodytext.control.table.Cell;
import kr.dogfoot.hwplib.object.bodytext.control.table.Row;
import kr.dogfoot.hwplib.object.bodytext.paragraph.Paragraph;
import kr.dogfoot.hwplib.object.bodytext.paragraph.text.ParaText;
import kr.dogfoot.hwplib.reader.HWPReader;
import kr.dogfoot.hwplib.tool.objectfinder.ControlFilter;
import kr.dogfoot.hwplib.tool.objectfinder.ControlFinder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


@Service
public class NewsServiceImpl implements NewsService {
    private static ResourceLoader resourceLoader;

    public NewsServiceImpl(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public String read(String fileName) throws Exception {
        Resource resource = resourceLoader.getResource("classpath:static/NewForm.hwp");
        HWPFile file = HWPReader.fromFile(resource.getFile());
        ArrayList<Control> result = ControlFinder.find(file, new ControlFilter(){
            @Override
            public boolean isMatched(Control control, Paragraph paragraph, Section section) {
                return control.getType() == ControlType.Table;
            }
        });

        if(result != null && !result.isEmpty()) {
            Control control = result.get(0);
            ControlTable table = (ControlTable) control;

            for(Row row : table.getRowList()) {
                for(Cell cell : row.getCellList()) {
                    System.out.print(cell.getParagraphList().getNormalString() + " | ");
                }
                System.out.println();
            }
        }
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
