package com.fasttime.domain.resume.service;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.domain.resume.dto.ResumeDeleteServiceRequest;
import com.fasttime.domain.resume.dto.ResumeRequestDto;
import com.fasttime.domain.resume.dto.ResumeResponseDto;
import com.fasttime.domain.resume.dto.ResumeUpdateServiceRequest;
import com.fasttime.domain.resume.entity.Resume;
import com.fasttime.domain.resume.exception.NoResumeWriterException;
import com.fasttime.domain.resume.exception.ResumeNotFoundException;
import com.fasttime.domain.resume.repository.ResumeRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final MemberService memberService;

    public ResumeResponseDto getResume(Long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new ResumeNotFoundException(id));

        return ResumeResponseDto.builder()
                .id(resume.getId())
                .title(resume.getTitle())
                .content(resume.getContent())
                .writer(resume.getWriter().getNickname())
                .likeCount(resume.getLikeCount())
                .viewCount(resume.getViewCount())
                .build();
    }

    public ResumeResponseDto createResume(ResumeRequestDto requestDto, Long memberId) {
        Member member = memberService.getMember(memberId);
        final Resume newResume = Resume.builder()
                .title(requestDto.title())
                .content(requestDto.content())
                .writer(member)
                .build();
        Resume createdResume = resumeRepository.save(newResume);

        return ResumeResponseDto.builder()
                .id(createdResume.getId())
                .title(createdResume.getTitle())
                .content(createdResume.getContent())
                .writer(createdResume.getWriter().getNickname())
                .likeCount(createdResume.getLikeCount())
                .viewCount(createdResume.getViewCount())
                .build();
    }

    public ResumeResponseDto updateResume(ResumeUpdateServiceRequest request) {
        final Member requestMember = memberService.getMember(
                request.memberId());
        Resume resume = findResumeById(request.resumeId());

        isWriter(requestMember, resume);

        resume.updateResume(request.title(), request.content());
        resumeRepository.save(resume);

        return ResumeResponseDto.builder()
                .id(resume.getId())
                .title(resume.getTitle())
                .content(resume.getContent())
                .writer(resume.getWriter().getNickname())
                .likeCount(resume.getLikeCount())
                .viewCount(resume.getViewCount())
                .build();
    }

    public void delete(ResumeDeleteServiceRequest deleteRequest) {
        final Member deleteRequestMember = memberService.getMember(
                deleteRequest.requestUserId());
        final Resume resume = findResumeById(deleteRequest.resumeId());
        isWriter(deleteRequestMember, resume);
        resume.delete(LocalDateTime.now());
        resumeRepository.save(resume);
    }

    private void isWriter(Member requestMember, Resume resume) {
        if (!requestMember.getId().equals(resume.getWriter().getId())) {
            throw new NoResumeWriterException(
                    String.format("작성자가 아닙니다. request user id = %d, resume writer id = %d",
                            requestMember.getId(), resume.getWriter().getId()));
        }
    }

    private Resume findResumeById(Long resumeId) {
        return resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResumeNotFoundException(resumeId));
    }
}
