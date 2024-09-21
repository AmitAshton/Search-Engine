package com.handson.searchengine.controller;

import com.handson.searchengine.crawler.Crawler;
import com.handson.searchengine.kafka.Producer;
import com.handson.searchengine.model.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Random;

@RestController
@RequestMapping("/api")
public class AppController {
    protected final Log logger = LogFactory.getLog(getClass());
    private static final int ID_LENGTH = 6;
    private Random random = new Random();
    @Autowired
    Crawler crawler;

    @Autowired
    Producer producer;

    @RequestMapping(value = "/crawl", method = RequestMethod.POST)
    public String crawl(@RequestBody CrawlerRequest request) {
        String crawlId = generateCrawlId();
        if (!request.getUrl().startsWith("http")) {
            request.setUrl("https://" + request.getUrl());
        }
        new Thread( () -> {
            try {
                crawler.crawl(crawlId, request);
            }catch (Exception e){
                logger.error(e.getMessage(), e);
            }
        }).start();
        return crawlId;
    }

    @RequestMapping(value = "/crawl/{crawlId}", method = RequestMethod.GET)
    public CrawlStatusOut getCrawl(@PathVariable String crawlId) throws IOException, InterruptedException {
        return crawler.getCrawlInfo(crawlId);
    }

    private String generateCrawlId() {
        String charPool = "ABCDEFHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < ID_LENGTH; i++) {
            res.append(charPool.charAt(random.nextInt(charPool.length())));
        }
        return res.toString();
    }
}
