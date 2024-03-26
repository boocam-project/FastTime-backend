package com.fasttime.domain.review.repository;

import com.fasttime.domain.review.dto.response.BootcampReviewSummaryDTO;
import com.fasttime.domain.review.entity.QReview;
import com.fasttime.domain.review.entity.Review;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class ReviewCustomRepositoryImpl implements ReviewCustomRepository {

    private final JPAQueryFactory queryFactory;

    public ReviewCustomRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<String> findAllBootcamps() {
        QReview review = QReview.review;
        return queryFactory
            .select(review.bootCamp.name)
            .distinct()
            .from(review)
            .fetch();
    }

    @Override
    public Page<BootcampReviewSummaryDTO> findBootcampReviewSummaries(Pageable pageable) {
        QReview review = QReview.review;

        List<BootcampReviewSummaryDTO> content = queryFactory
            .select(Projections.constructor(
                BootcampReviewSummaryDTO.class,
                review.bootCamp.name,
                review.rating.avg(),
                review.id.count()
            ))
            .from(review)
            .where(review.deletedAt.isNull())
            .groupBy(review.bootCamp.name)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .select(review.count())
            .from(review)
            .where(review.deletedAt.isNull())
            .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public int countByBootcamp(String bootcampName) {
        QReview review = QReview.review;
        return (int) queryFactory
            .selectFrom(review)
            .where(review.bootCamp.name.eq(bootcampName))
            .fetchCount();
    }

    @Override
    public double findAverageRatingByBootcamp(String bootcampName) {
        QReview review = QReview.review;
        Double avg = queryFactory
            .select(review.rating.avg())
            .from(review)
            .where(review.bootCamp.name.eq(bootcampName))
            .fetchOne();
        return avg != null ? avg : 0.0;
    }

    @Override
    public Page<Review> findByBootcampName(String bootcampName, Pageable pageable) {
        QReview review = QReview.review;
        List<Review> content = queryFactory
            .selectFrom(review)
            .where(review.bootCamp.name.eq(bootcampName), review.deletedAt.isNull())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .selectFrom(review)
            .where(review.bootCamp.name.eq(bootcampName), review.deletedAt.isNull())
            .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public void deleteReviewsOlderThan7Days(LocalDateTime cutoffDate) {
        QReview review = QReview.review;
        queryFactory
            .delete(review)
            .where(review.deletedAt.isNotNull().and(review.deletedAt.loe(cutoffDate)))
            .execute();
    }

    @Override
    public Page<Review> findAllReviews(Pageable pageable) {
        QReview review = QReview.review;
        List<Review> content = queryFactory
            .selectFrom(review)
            .where(review.deletedAt.isNull())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .selectFrom(review)
            .where(review.deletedAt.isNull())
            .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }
}
