package com.fasttime.domain.reference.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasttime.domain.reference.dto.request.ReferencePageRequestDto;
import com.fasttime.domain.reference.dto.request.ReferenceSearchRequestDto;
import com.fasttime.domain.reference.dto.response.ActivityPageResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
public class ReferenceServiceTest {

    @InjectMocks
    private ReferenceService referenceService;

    @Nested
    @DisplayName("searchActivities()은")
    class Context_searchActivities {

        @Test
        @DisplayName("대외활동 목록을 조회할 수 있다.")
        void _willSuccess() {
            // given
            ReferenceSearchRequestDto referenceSearchRequestDto = ReferenceSearchRequestDto.builder()
                .keyword(null)
                .before(true)
                .during(true)
                .closed(true)
                .build();
            ReferencePageRequestDto referencePageRequestDto = ReferencePageRequestDto.builder()
                .orderBy(null)
                .page(0)
                .pageSize(10)
                .build();

            // when
            ActivityPageResponseDto result = referenceService.searchActivities(
                referenceSearchRequestDto,
                referencePageRequestDto
            );

            // then
            assertThat(result.totalPages()).isEqualTo(1);
            assertThat(result.isLastPage()).isEqualTo(true);
            assertThat(result.totalActivity()).isEqualTo(2);
            assertThat(result.activities().size()).isEqualTo(2);
        }
    }
}
