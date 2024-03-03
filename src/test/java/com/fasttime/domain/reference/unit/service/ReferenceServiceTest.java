package com.fasttime.domain.reference.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.fasttime.domain.reference.dto.request.ReferencePageRequestDto;
import com.fasttime.domain.reference.dto.request.ReferenceSearchRequestDto;
import com.fasttime.domain.reference.dto.response.ActivityPageResponseDto;
import com.fasttime.domain.reference.dto.response.ActivityResponseDto;
import com.fasttime.domain.reference.dto.response.CompetitionPageResponseDto;
import com.fasttime.domain.reference.dto.response.CompetitionResponseDto;
import com.fasttime.domain.reference.entity.Activity;
import com.fasttime.domain.reference.entity.Competition;
import com.fasttime.domain.reference.entity.RecruitmentStatus;
import com.fasttime.domain.reference.repository.ActivityRepository;
import com.fasttime.domain.reference.repository.CompetitionRepository;
import com.fasttime.domain.reference.service.ReferenceService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
public class ReferenceServiceTest {

    @InjectMocks
    private ReferenceService referenceService;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private CompetitionRepository competitionRepository;

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
                .orderBy("d-day")
                .page(0)
                .pageSize(10)
                .build();

            Page<Activity> activityPage = new PageImpl<>(List.of(
                Activity.builder()
                    .id(1L)
                    .title("핀테크 IT 대외활동")
                    .organization("대외활동 협회")
                    .corporateType("기타")
                    .participate("대상 제한 없음")
                    .startDate(LocalDate.of(2024, 1, 1))
                    .endDate(LocalDate.of(2024, 1, 31))
                    .period("24.2.7 ~ 24.8.4")
                    .recruitment(20)
                    .area("지역 제한없음")
                    .preferredSkill("기타")
                    .homepageUrl("https://activities/1")
                    .field("기타, 멘토링")
                    .activityBenefit("활동비, 실무 교육")
                    .bonusBenefit("취업연계기회 제공")
                    .description("""
                        [채용연계형] 핀테크 개발자 양성과정 훈련생 모집 中
                                                
                        ▣ 채용연계형_K-Digital Training 훈련생 모집
                        ▣ 고용노동부 인증 우수 교육기관
                        ▣ K-DIGITAL TRAINING▣ 비전공자 맞춤 커리큘럼
                        ▣ 취업연계를 통한 성공적인 24년 취업 지원""")
                    .imageUrl("https://activities/1/images/1")
                    .status(RecruitmentStatus.CLOSED)
                    .build(),
                Activity.builder()
                    .id(2L)
                    .title("풀스택 IT 대외활동")
                    .organization("대외활동 협회")
                    .corporateType("기타")
                    .participate("대상 제한 없음")
                    .startDate(LocalDate.of(2024, 2, 21))
                    .endDate(LocalDate.of(2024, 2, 21).plusDays(90))
                    .period("24.3.7 ~ 24.9.4")
                    .recruitment(20)
                    .area("지역 제한없음")
                    .preferredSkill("기타")
                    .homepageUrl("https://activities/2")
                    .field("기타, 멘토링")
                    .activityBenefit("활동비, 실무 교육")
                    .bonusBenefit("취업연계기회 제공")
                    .description("""
                        [채용연계형] 풀스택 개발자 양성과정 훈련생 모집 中
                                                
                        ▣ 채용연계형_K-Digital Training 훈련생 모집
                        ▣ 고용노동부 인증 우수 교육기관
                        ▣ K-DIGITAL TRAINING
                        ▣ 비전공자 맞춤 커리큘럼
                        ▣ 취업연계를 통한 성공적인 24년 취업 지원""")
                    .imageUrl("https://activities/2/images/2")
                    .status(RecruitmentStatus.DURING)
                    .build()
            ));

            given(activityRepository.findAllBySearchConditions(
                any(ReferenceSearchRequestDto.class),
                any(Pageable.class)
            )).willReturn(activityPage);

            // when
            ActivityPageResponseDto result = referenceService.searchActivities(
                referenceSearchRequestDto,
                referencePageRequestDto.toPageable()
            );

            // then
            assertThat(result.totalPages()).isEqualTo(1);
            assertThat(result.isLastPage()).isEqualTo(true);
            assertThat(result.totalActivities()).isEqualTo(2);
            assertThat(result.activities().size()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("searchCompetitions()은")
    class Context_searchCompetitions {

        @Test
        @DisplayName("공모전 목록을 조회할 수 있다.")
        void _willSuccess() {
            // given
            ReferenceSearchRequestDto referenceSearchRequestDto = ReferenceSearchRequestDto.builder()
                .keyword(null)
                .before(true)
                .during(true)
                .closed(true)
                .build();
            ReferencePageRequestDto referencePageRequestDto = ReferencePageRequestDto.builder()
                .orderBy("d-day")
                .page(0)
                .pageSize(10)
                .build();

            Page<Competition> competitionPage = new PageImpl<>(List.of(
                Competition.builder()
                    .id(1L)
                    .title("핀테크 IT 공모전")
                    .organization("공모전 협회")
                    .corporateType("기타")
                    .participate("대상 제한 없음")
                    .awardScale("450만 원")
                    .startDate(LocalDate.of(2024, 1, 1))
                    .endDate(LocalDate.of(2024, 1, 31))
                    .homepageUrl("https://competitions/1")
                    .activityBenefit("기타")
                    .bonusBenefit("-")
                    .description("""
                        [공모개요] '핀테크 공모전'을 개최하오니 많은 관심과 참여 부탁드립니다.
                        """)
                    .imageUrl("https://competitions/1/images/1")
                    .status(RecruitmentStatus.CLOSED)
                    .build(),
                Competition.builder()
                    .id(2L)
                    .title("풀스택 IT 공모전")
                    .organization("공모전 협회")
                    .corporateType("기타")
                    .participate("대상 제한 없음")
                    .awardScale("450만 원")
                    .startDate(LocalDate.of(2024, 1, 1))
                    .endDate(LocalDate.of(2024, 1, 31))
                    .homepageUrl("https://competitions/2")
                    .activityBenefit("기타")
                    .bonusBenefit("-")
                    .description("""
                        [공모개요] '풀스택 공모전'을 개최하오니 많은 관심과 참여 부탁드립니다.
                        """)
                    .imageUrl("https://competitions/2/images/2")
                    .status(RecruitmentStatus.CLOSED)
                    .build()
            ));

            given(competitionRepository.findAllBySearchConditions(
                any(ReferenceSearchRequestDto.class),
                any(Pageable.class)
            )).willReturn(competitionPage);

            // when
            CompetitionPageResponseDto result = referenceService.searchCompetitions(
                referenceSearchRequestDto,
                referencePageRequestDto.toPageable()
            );

            // then
            assertThat(result.totalPages()).isEqualTo(1);
            assertThat(result.isLastPage()).isEqualTo(true);
            assertThat(result.totalCompetitions()).isEqualTo(2);
            assertThat(result.competitions().size()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("getActivity()은")
    class Context_getActivity {

        @Test
        @DisplayName("대외활동 상세 정보를 조회할 수 있다.")
        void _willSuccess() {
            // given
            Activity activity = Activity.builder()
                .id(1L)
                .title("핀테크 IT 대외활동")
                .organization("대외활동 협회")
                .corporateType("기타")
                .participate("대상 제한 없음")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 31))
                .period("24.2.7 ~ 24.8.4")
                .recruitment(20)
                .area("지역 제한없음")
                .preferredSkill("기타")
                .homepageUrl("https://activities/1")
                .field("기타, 멘토링")
                .activityBenefit("활동비, 실무 교육")
                .bonusBenefit("취업연계기회 제공")
                .description("""
                    [채용연계형] 핀테크 개발자 양성과정 훈련생 모집 中
                                            
                    ▣ 채용연계형_K-Digital Training 훈련생 모집
                    ▣ 고용노동부 인증 우수 교육기관
                    ▣ K-DIGITAL TRAINING▣ 비전공자 맞춤 커리큘럼
                    ▣ 취업연계를 통한 성공적인 24년 취업 지원""")
                .imageUrl("https://activities/1/images/1")
                .status(RecruitmentStatus.CLOSED)
                .build();

            given(activityRepository.findById(any(Long.TYPE))).willReturn(Optional.of(activity));

            // when
            ActivityResponseDto result = referenceService.getActivity(1L);

            // then
            assertThat(result.title()).isEqualTo("핀테크 IT 대외활동");
        }
    }

    @Nested
    @DisplayName("getCompetition()은")
    class Context_getCompetition {

        @Test
        @DisplayName("공모전 상세 정보를 조회할 수 있다.")
        void _willSuccess() {
            // given
            Competition competition = Competition.builder()
                .id(1L)
                .title("핀테크 IT 공모전")
                .organization("공모전 협회")
                .corporateType("기타")
                .participate("대상 제한 없음")
                .awardScale("450만 원")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 31))
                .homepageUrl("https://competitions/1")
                .activityBenefit("기타")
                .bonusBenefit("-")
                .description("""
                    [공모개요] '핀테크 공모전'을 개최하오니 많은 관심과 참여 부탁드립니다.
                    """)
                .imageUrl("https://competitions/1/images/1")
                .status(RecruitmentStatus.CLOSED)
                .build();

            given(competitionRepository.findById(any(Long.TYPE)))
                .willReturn(Optional.of(competition));

            // when
            CompetitionResponseDto result = referenceService.getCompetition(1L);

            // then
            assertThat(result.title()).isEqualTo("핀테크 IT 공모전");
        }
    }
}
