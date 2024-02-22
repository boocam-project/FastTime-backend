package com.fasttime.domain.reference.repository;

import com.fasttime.domain.reference.dto.request.ReferenceSearchRequestDto;
import com.fasttime.domain.reference.entity.Activity;
import com.fasttime.domain.reference.entity.Competition;
import com.fasttime.domain.reference.entity.QActivity;
import com.fasttime.domain.reference.entity.QCompetition;
import com.fasttime.domain.reference.entity.RecruitmentStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CompetitionCustomRepositoryImpl implements CompetitionCustomRepository {

    private final JPAQueryFactory queryFactory;

    private final QCompetition qCompetition = QCompetition.competition;

    @Override
    public Page<Competition> findAllBySearchConditions(
        ReferenceSearchRequestDto referenceSearchRequestDto,
        Pageable pageable
    ) {
        // TODO 정렬 기능 추가
        List<Competition> content = queryFactory
            .selectFrom(qCompetition)
            .where(createSearchConditionsBuilder(referenceSearchRequestDto))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(qCompetition.count())
            .from(qCompetition)
            .where(createSearchConditionsBuilder(referenceSearchRequestDto));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanBuilder createSearchConditionsBuilder(
        ReferenceSearchRequestDto referenceSearchRequestDto) {
        boolean isFindAllByKeyword = referenceSearchRequestDto.keyword() != null;
        boolean isFindAllByStatusNotBefore = !referenceSearchRequestDto.before();
        boolean isFindAllByStatusNotDuring = !referenceSearchRequestDto.during();
        boolean isFindAllByStatusNotClosed = !referenceSearchRequestDto.closed();

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (isFindAllByKeyword) {
            booleanBuilder.and(qCompetition.title.contains(referenceSearchRequestDto.keyword()));
        }
        if (isFindAllByStatusNotBefore) {
            booleanBuilder.and(qCompetition.status.ne(RecruitmentStatus.BEFORE));
        }
        if (isFindAllByStatusNotDuring) {
            booleanBuilder.and(qCompetition.status.ne(RecruitmentStatus.DURING));
        }
        if (isFindAllByStatusNotClosed) {
            booleanBuilder.and(qCompetition.status.ne(RecruitmentStatus.CLOSED));
        }

        return booleanBuilder;
    }
}
