package com.fasttime.domain.resume.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.domain.resume.dto.ResumeRequestDto;
import com.fasttime.domain.resume.dto.ResumeResponseDto;
import com.fasttime.domain.resume.dto.ResumeUpdateServiceRequest;
import com.fasttime.domain.resume.entity.Resume;
import com.fasttime.domain.resume.exception.NoResumeWriterException;
import com.fasttime.domain.resume.exception.ResumeNotFoundException;
import com.fasttime.domain.resume.repository.ResumeRepository;
import com.fasttime.domain.resume.service.ResumeService;
import java.util.Optional;
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

    public static final String MOCK_RESUME_TITLE = "Resume test1";
    public static final String MOCK_RESUME_CONTENT = "# This is Resume";
    public static final long MOCK_RESUME_ID = 1L;
    @Mock
    private MemberService memberService;
    @InjectMocks
    private ResumeService resumeService;
    @Mock
    private ResumeRepository resumeRepository;

    @DisplayName("createResume()는")
    @Nested
    class Context_createResume {

        @DisplayName("자기소개서를 DB에 성공적으로 저장한다.")
        @Test
        void _willSuccess() {
            // given
            Member member = Member.builder().id(1L).nickname("testName").build();
            ResumeRequestDto requestDto = new ResumeRequestDto(MOCK_RESUME_TITLE,
                    MOCK_RESUME_CONTENT);
            Resume mockResume = createMockResume(member);

            given(memberService.getMember(1L)).willReturn(member);
            given(resumeRepository.save(any(Resume.class))).willReturn(mockResume);

            // when
            ResumeResponseDto response = resumeService.createResume(requestDto, 1L);

            // then
            assertThat(response).extracting("id", "title", "content", "writer", "rating")
                    .containsExactly(1L, MOCK_RESUME_TITLE, MOCK_RESUME_CONTENT, "testName", 0);
        }

    }

    @DisplayName("updateResume()는")
    @Nested
    class Context_updateResume {

        @DisplayName("자기소개서를 성공적으로 편집한다.")
        @Test
        void _willSuccess() {
            // given
            Member member = Member.builder().id(1L).nickname("testName").build();
            Resume mockResume = createMockResume(member);

            String updatedTitle = "updated title";
            String updatedContent = "updated content";
            ResumeUpdateServiceRequest updateRequest = new ResumeUpdateServiceRequest(
                    MOCK_RESUME_ID, 1L,
                    updatedTitle, updatedContent);

            given(memberService.getMember(1L)).willReturn(member);
            given(resumeRepository.findById(MOCK_RESUME_ID)).willReturn(Optional.of(mockResume));

            // when
            ResumeResponseDto response = resumeService.updateResume(updateRequest);
            // then
            assertThat(response).extracting("id", "title", "content", "writer", "rating")
                    .containsExactly(1L, updatedTitle, updatedContent, "testName", 0);
        }

        @DisplayName("자기소개서 작성자가 아닌 경우 NoResumeWriterException을 반환한다.")
        @Test
        void resume_validateFail_throwIllegalArgumentException() {
            Member writer = Member.builder().id(1L).nickname("testName").build();
            Member notAuthorizedMember = Member.builder().id(221L).build();
            Resume mockResume = createMockResume(writer);
            ResumeUpdateServiceRequest updateRequest = new ResumeUpdateServiceRequest(
                    MOCK_RESUME_ID, notAuthorizedMember.getId(),
                    "updateTitle", "updateContent");

            given(memberService.getMember(anyLong())).willReturn(notAuthorizedMember);
            given(resumeRepository.findById(anyLong())).willReturn(Optional.of(mockResume));

            assertThatThrownBy(() -> resumeService.updateResume(updateRequest))
                    .isInstanceOf(NoResumeWriterException.class);

        }

        @DisplayName("수정할 자기소개서가 없는 경우 ResumeNotFoundException을 반환한다.")
        @Test
        void resume_notExist_throwExceptoin() {
            ResumeUpdateServiceRequest request = new ResumeUpdateServiceRequest(MOCK_RESUME_ID, 1L,
                    "updateTitle", "updateContent");

            given(resumeRepository.findById(anyLong())).willReturn(Optional.empty());

            assertThatThrownBy(() -> resumeService.updateResume(request))
                    .isInstanceOf(ResumeNotFoundException.class);
        }


    }

    @DisplayName("deleteResume()는")
    @Nested
    class Context_deleteResume {

    }

    @DisplayName("getResume()는")
    @Nested
    class Context_getResume {

        @DisplayName("자기소개서를 성공적으로 불러온다.")
        @Test
        void _willSuccess() {
            // given
            Member member = Member.builder().id(1L).nickname("testName").build();
            Resume resumeInDb = createMockResume(member);
            // when
            // then
        }
    }

    private static Resume createMockResume(Member member) {
        return Resume.builder()
                .id(MOCK_RESUME_ID)
                .title(MOCK_RESUME_TITLE)
                .content(MOCK_RESUME_CONTENT)
                .writer(member)
                .rating(0)
                .build();
    }
}