package com.fasttime.domain.reference.repository;

import com.fasttime.domain.reference.dto.request.ReferenceSearchRequestDto;
import com.fasttime.domain.reference.entity.Activity;
import com.fasttime.domain.reference.entity.Competition;
import com.fasttime.domain.reference.entity.QActivity;
import com.fasttime.domain.reference.entity.QCompetition;
import com.fasttime.domain.reference.entity.RecruitmentStatus;
import com.fasttime.global.util.QueryDslUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        List<Competition> content = queryFactory
            .selectFrom(qCompetition)
            .where(createSearchConditionsBuilder(referenceSearchRequestDto))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(getAllOrderSpecifiers(pageable).toArray(OrderSpecifier[]::new))
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(qCompetition.count())
            .from(qCompetition)
            .where(createSearchConditionsBuilder(referenceSearchRequestDto));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public List<Competition> findAllByRecruitmentStart(LocalDate now) {
        return queryFactory.selectFrom(qCompetition)
            .where(qCompetition.status.eq(RecruitmentStatus.BEFORE)
                .and(qCompetition.startDate.goe(now)))
            .fetch();
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

    private List<OrderSpecifier<?>> getAllOrderSpecifiers(Pageable pageable) {
        List<OrderSpecifier<?>> orders = new LinkedList<>();
        if (!pageable.getSort().isEmpty()) {
            for (Sort.Order order : pageable.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                switch (order.getProperty()) {
                    case "endDate":
                        orders.add(QueryDslUtil.getSortedColumn(direction, qCompetition, "endDate"));
                    case "id":
                        orders.add(QueryDslUtil.getSortedColumn(direction, qCompetition, "id"));
                    default:
                        orders.add(QueryDslUtil.getSortedColumn(Order.ASC, qCompetition, "endDate"));
                }
            }
        }

        return orders;
    }
}
