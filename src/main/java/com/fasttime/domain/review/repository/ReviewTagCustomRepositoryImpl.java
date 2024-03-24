package com.fasttime.domain.review.repository;

import com.fasttime.domain.review.entity.QReview;
import com.fasttime.domain.review.entity.QReviewTag;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class ReviewTagCustomRepositoryImpl implements ReviewTagCustomRepository {

    private final JPAQueryFactory queryFactory;

    public ReviewTagCustomRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<Object[]> countTagsByBootcampGroupedByTagId(String bootcampName) {
        QReviewTag reviewTag = QReviewTag.reviewTag;
        QReview review = QReview.review;

        List<Tuple> result = queryFactory
            .select(reviewTag.tag.id, reviewTag.id.count())
            .from(reviewTag)
            .join(reviewTag.review, review)
            .where(review.bootCamp.name.eq(bootcampName).and(review.deletedAt.isNull()))
            .groupBy(reviewTag.tag.id)
            .fetch();

        return result.stream()
            .map(tuple -> new Object[]{tuple.get(0, Long.class), tuple.get(1, Long.class)})
            .collect(Collectors.toList());
    }
}
