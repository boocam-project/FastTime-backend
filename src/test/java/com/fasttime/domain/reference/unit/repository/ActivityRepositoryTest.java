package com.fasttime.domain.reference.unit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasttime.domain.reference.dto.request.ReferencePageRequestDto;
import com.fasttime.domain.reference.dto.request.ReferenceSearchRequestDto;
import com.fasttime.domain.reference.entity.Activity;
import com.fasttime.domain.reference.entity.RecruitmentStatus;
import com.fasttime.domain.reference.repository.ActivityRepository;
import com.fasttime.global.config.JpaTestConfig;
import com.fasttime.global.config.QueryDslTestConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

@DataJpaTest
@Import({JpaTestConfig.class, QueryDslTestConfig.class})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ActivityRepositoryTest {

    @Autowired
    private ActivityRepository activityRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Nested
    @DisplayName("findAllBySearchConditions()는 ")
    class Context_findAllBySearchConditions {

        @Test
        @DisplayName("대외활동을 조회할 수 있다.")
        void _willSuccess() {
            // given
            activityRepository.save(Activity.builder()
                .id(1L)
                .title("핀테크 IT 대외활동")
                .organization("대외활동 협회")
                .corporateType("기타")
                .participate("대상 제한 없음")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2023, 1, 31))
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
                .build());
            activityRepository.save(Activity.builder()
                .id(2L)
                .title("풀스택 IT 대외활동")
                .organization("대외활동 협회")
                .corporateType("기타")
                .participate("대상 제한 없음")
                .startDate(LocalDate.of(2024, 2, 21))
                .endDate(LocalDate.now().plusDays(90))
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
                .build());
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

            // when
            Page<Activity> result = activityRepository.findAllBySearchConditions(
                referenceSearchRequestDto,
                referencePageRequestDto.toPageable()
            );

            // then
            assertThat(result.getTotalPages()).isEqualTo(1);
            assertThat(result.isLast()).isEqualTo(true);
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getContent().size()).isEqualTo(2);
            assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
            assertThat(result.getContent().get(1).getId()).isEqualTo(2L);
        }
    }

    @BeforeEach
    public void reset() {
        entityManager.flush();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        activityRepository.deleteAll();
        entityManager.createNativeQuery("TRUNCATE TABLE activity").executeUpdate();
        entityManager
            .createNativeQuery("ALTER TABLE activity ALTER COLUMN `id` RESTART WITH 1")
            .executeUpdate();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }
}
