package com.fasttime.domain.resume.service;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.domain.resume.dto.ResumeRequestDto;
import com.fasttime.domain.resume.dto.ResumeResponseDto;
import com.fasttime.domain.resume.entity.Resume;
import com.fasttime.domain.resume.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final MemberService memberService;
    public ResumeResponseDto createResume(ResumeRequestDto requestDto, Long memberId) {
        Member member = memberService.getMember(memberId);

        final Resume newResume = Resume.createNewResume(member, requestDto.title(), requestDto.content());
        Resume createdResume = resumeRepository.save(newResume);


        return ResumeResponseDto.builder()
                .id(createdResume.getId())
                .title(createdResume.getTitle())
                .content(createdResume.getContent())
                .writer(createdResume.getWriter().getNickname())
                .rating(createdResume.getRating())
                .build();
    }

}
