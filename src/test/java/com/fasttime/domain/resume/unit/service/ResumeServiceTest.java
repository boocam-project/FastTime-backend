package com.fasttime.domain.resume.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.domain.resume.dto.ResumeRequestDto;
import com.fasttime.domain.resume.dto.ResumeResponseDto;
import com.fasttime.domain.resume.entity.Resume;
import com.fasttime.domain.resume.repository.ResumeRepository;
import com.fasttime.domain.resume.service.ResumeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Slf4j
@ExtendWith(MockitoExtension.class)
class ResumeServiceTest {

    @Mock
    private MemberService memberService;
    @InjectMocks
    private ResumeService resumeService;
    @Mock
    private ResumeRepository resumeRepository;

    @Nested
    class Context_createResume {

        @DisplayName("자기소개서를 DB에 성공적으로 저장한다.")
        @Test
        void _willSuccess() {
            // given
            Member member = Member.builder().id(1L).nickname("testName").build();
            ResumeRequestDto requestDto = new ResumeRequestDto("Resume test1", "# This is Resume");
            Resume mockResume = Resume.builder()
                    .id(1L)
                    .title("Resume test1")
                    .content("# This is Resume")
                    .writer(member)
                    .rating(0)
                    .build();

            given(memberService.getMember(1L)).willReturn(member);
            given(resumeRepository.save(any(Resume.class))).willReturn(mockResume);

            // when
            ResumeResponseDto response = resumeService.createResume(requestDto, 1L);

            // then
            assertThat(response).extracting("id", "title", "content", "writer", "rating")
                    .containsExactly(1L, "Resume test1", "# This is Resume", "testName", 0);
        }
    }
}