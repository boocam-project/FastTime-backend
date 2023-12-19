package com.fasttime.domain.article.repository;


import static com.fasttime.domain.article.entity.QArticle.article;
import static com.fasttime.domain.comment.entity.QComment.comment;

import com.fasttime.domain.article.service.usecase.ArticleQueryUseCase.ArticlesSearchRequestServiceDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;

public class ArticleRepositoryImpl implements ArticleRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public ArticleRepositoryImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<ArticleQueryResponse> search(ArticlesSearchRequestServiceDto searchCondition) {

        return jpaQueryFactory
            .select(Projections.fields(ArticleQueryResponse.class,
                article.id,
                article.member.id.as("memberId"),
                article.member.nickname,
                article.title,
                article.content.content,
                article.anonymity,
                article.commentCount,
                article.likeCount,
                article.hateCount,
                article.createdAt,
                article.updatedAt,
                article.deletedAt
            ))
            .from(article)
            .where(createArticleSearchConditionBuilder(searchCondition))
            .offset((long) searchCondition.page() * searchCondition.pageSize())
            .limit(searchCondition.pageSize())
            .orderBy(orderSpecifierProvider(searchCondition))
            .fetch();
    }

    private BooleanBuilder createArticleSearchConditionBuilder(
        ArticlesSearchRequestServiceDto searchCondition) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (!isEmpty(searchCondition.title())) {
            booleanBuilder.and(article.title.contains(searchCondition.title()));
        }

        if (!isEmpty(searchCondition.nickname())) {
            booleanBuilder.and(article.member.nickname.contains(searchCondition.nickname()));
        }

        if (searchCondition.likeCount() > 0) {
            booleanBuilder.and(article.likeCount.gt(searchCondition.likeCount()));
        }

        booleanBuilder.and(article.deletedAt.isNull());

        return booleanBuilder;
    }

    private boolean isEmpty(String validateTarget) {
        return validateTarget == null || validateTarget.isBlank();
    }

    private OrderSpecifier<?> orderSpecifierProvider(
        ArticlesSearchRequestServiceDto searchCondition) {
        Order direction = searchCondition.isAscending() ? Order.ASC : Order.DESC;

        return switch (searchCondition.orderByType()) {
            case "commentCount" -> new OrderSpecifier<>(direction, comment.count());
            case "likeCount" -> new OrderSpecifier<>(direction, article.likeCount);
            case null, default -> new OrderSpecifier<>(direction, article.createdAt);
        };
    }
}
