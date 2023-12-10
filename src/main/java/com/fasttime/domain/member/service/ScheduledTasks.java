package com.fasttime.domain.member.service;

import com.fasttime.domain.member.repository.MemberRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduledTasks {

    private final MemberRepository memberRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteExpiredMembers() {

        LocalDateTime oneYearAgo = LocalDateTime.ofInstant(
            Instant.now().minusSeconds(60 * 60 * 24 * 365), ZoneId.of("UTC"));

        memberRepository.deleteByDeletedAtBefore(oneYearAgo);
    }
}
