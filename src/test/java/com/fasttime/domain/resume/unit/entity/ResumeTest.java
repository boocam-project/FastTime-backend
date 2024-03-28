package com.fasttime.domain.resume.unit.entity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.resume.entity.Resume;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


@Slf4j
public class ResumeTest {

    @DisplayName("view()는")
    @Nested
    class Context_Resume_View {

        @DisplayName("Resume의 viewCount의 값을 1 올린다")
        @Test
        void _Success() {
            // given
            Resume resume = Resume.builder()
                    .id(1L)
                    .title("test")
                    .content("test content")
                    .writer(Member.builder()
                            .id(1L).nickname("testName").build())
                    .build();
            // when
            resume.view();
            // then
            assertThat(resume.getViewCount()).isEqualTo(1);
        }
    }
}
