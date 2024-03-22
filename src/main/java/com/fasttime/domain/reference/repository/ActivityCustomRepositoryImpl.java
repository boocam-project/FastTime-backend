package com.fasttime.domain.reference.repository;

import com.fasttime.domain.reference.dto.request.ReferenceSearchRequestDto;
import com.fasttime.domain.reference.entity.Activity;
import com.fasttime.domain.reference.entity.QActivity;
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
public class ActivityCustomRepositoryImpl implements ActivityCustomRepository {

    private final JPAQueryFactory queryFactory;

    private final QActivity qActivity = QActivity.activity;

    @Override
    public Page<Activity> findAllBySearchConditions(
        ReferenceSearchRequestDto referenceSearchRequestDto,
        Pageable pageable
    ) {
        List<Activity> content = queryFactory
            .selectFrom(qActivity)
            .where(createSearchConditionsBuilder(referenceSearchRequestDto))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(getAllOrderSpecifiers(pageable).toArray(OrderSpecifier[]::new))
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(qActivity.count())
            .from(qActivity)
            .where(createSearchConditionsBuilder(referenceSearchRequestDto));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public List<Activity> findAllByRecruitmentStart(LocalDate now) {
        return queryFactory.selectFrom(qActivity)
            .where(qActivity.status.eq(RecruitmentStatus.BEFORE)
                .and(qActivity.startDate.goe(now)))
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
            booleanBuilder.and(qActivity.title.contains(referenceSearchRequestDto.keyword()));
        }
        if (isFindAllByStatusNotBefore) {
            booleanBuilder.and(qActivity.status.ne(RecruitmentStatus.BEFORE));
        }
        if (isFindAllByStatusNotDuring) {
            booleanBuilder.and(qActivity.status.ne(RecruitmentStatus.DURING));
        }
        if (isFindAllByStatusNotClosed) {
            booleanBuilder.and(qActivity.status.ne(RecruitmentStatus.CLOSED));
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
                        orders.add(QueryDslUtil.getSortedColumn(direction, qActivity, "endDate"));
                    case "id":
                        orders.add(QueryDslUtil.getSortedColumn(direction, qActivity, "id"));
                    default:
                        orders.add(QueryDslUtil.getSortedColumn(Order.ASC, qActivity, "endDate"));
                }
            }
        }

        return orders;
    }
}
