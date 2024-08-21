package com.gongyeon.io.netkim.controller;

import com.gongyeon.io.netkim.model.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/")
public class NewsController {
    private final NewsService newsService;

    @Autowired
    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("")
    public String read(@RequestBody Map<String, String> str) throws Exception {
        String location = str.get("location");
        return newsService.read(location);
    }

    @PostMapping("")
    public String write(@RequestBody Map<String, String> str){
        String location = str.get("location");
        String prfId = str.get("prfId");
        newsService.write(location, prfId);
        return "Complete";
    }
}
