package com.fasttime.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduledTasks {

    private final MemberService memberService;


    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteExpiredMembers() {
        memberService.deleteExpiredSoftDeletedMembers();
    }
}
