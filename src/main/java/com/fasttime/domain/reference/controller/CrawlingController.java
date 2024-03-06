package com.fasttime.domain.reference.controller;

import com.fasttime.domain.reference.service.CrawlingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2")
public class CrawlingController {

    private final CrawlingService crawlingService;

    @GetMapping("/activity/init")
    public void activityInit() throws InterruptedException {
        crawlingService.initActivity();
    }
    @GetMapping("/activity/new")
    public void activityNew() throws InterruptedException {
        crawlingService.updateNewActivity();
    }
    @GetMapping("/activity/done")
    public void activityDone() throws InterruptedException {
        crawlingService.updateDoneActivity();
    }

    @GetMapping("/competition/init")
    public void competitionInit() throws InterruptedException {
        crawlingService.initCompetition();
    }

    @GetMapping("/competition/new")
    public void competitionNew() throws InterruptedException {
        crawlingService.updateNewCompetition();
    }
    @GetMapping("/competition/done")
    public void competitionDone() throws InterruptedException {
        crawlingService.updateDoneCompetition();
    }

}
