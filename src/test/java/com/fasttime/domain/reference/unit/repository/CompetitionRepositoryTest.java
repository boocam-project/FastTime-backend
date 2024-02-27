package com.fasttime.domain.reference.unit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasttime.domain.reference.dto.request.ReferencePageRequestDto;
import com.fasttime.domain.reference.dto.request.ReferenceSearchRequestDto;
import com.fasttime.domain.reference.entity.Activity;
import com.fasttime.domain.reference.entity.Competition;
import com.fasttime.domain.reference.entity.RecruitmentStatus;
import com.fasttime.domain.reference.repository.CompetitionRepository;
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
public class CompetitionRepositoryTest {

    @Autowired
    private CompetitionRepository competitionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Nested
    @DisplayName("findAllBySearchConditions()는 ")
    class Context_findAllBySearchConditions {

        @Test
        @DisplayName("공모전 목록을 조회할 수 있다.")
        void _willSuccess() {
            // given
            competitionRepository.save(Competition.builder()
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
                .build());
            competitionRepository.save(Competition.builder()
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
            Page<Competition> result = competitionRepository.findAllBySearchConditions(
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
        competitionRepository.deleteAll();
        entityManager.createNativeQuery("TRUNCATE TABLE competition").executeUpdate();
        entityManager
            .createNativeQuery("ALTER TABLE competition ALTER COLUMN `id` RESTART WITH 1")
            .executeUpdate();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }
}
