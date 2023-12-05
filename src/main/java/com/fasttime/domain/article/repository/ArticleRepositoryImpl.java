package com.fasttime.domain.article.repository;


import static com.fasttime.domain.article.entity.QArticle.article;

import com.fasttime.domain.article.service.usecase.ArticleQueryUseCase.ArticlesSearchServiceRequest;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import jakarta.persistence.EntityManager;

public class ArticleRepositoryImpl implements ArticleRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public ArticleRepositoryImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<ArticleQueryResponse> search(ArticlesSearchServiceRequest searchCondition) {
        return jpaQueryFactory
            .select(Projections.fields(ArticleQueryResponse.class,
                article.id,
                article.member.id.as("memberId"),
                article.member.nickname,
                article.title,
                article.anonymity,
                article.comments.size().as("commentCount"),
                article.likeCount,
                article.hateCount,
                article.createdAt,
                article.updatedAt,
                article.deletedAt
            ))
            .from(article)
            .where(createSearchConditionBuilder(searchCondition))
            .offset((long) searchCondition.getPage() * searchCondition.getPageSize())
            .limit(searchCondition.getPageSize())
            .orderBy(article.createdAt.desc())
            .fetch();
    }

    private BooleanBuilder createSearchConditionBuilder(ArticlesSearchServiceRequest searchCondition) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (!isEmpty(searchCondition.getTitle())) {
            booleanBuilder.and(article.title.contains(searchCondition.getTitle()));
        }

        if (!isEmpty(searchCondition.getNickname())) {
            booleanBuilder.and(article.member.nickname.contains(searchCondition.getNickname()));
        }

        if (searchCondition.getLikeCount() > 0) {
            booleanBuilder.and(article.likeCount.gt(searchCondition.getLikeCount()));
        }

        booleanBuilder.and(article.deletedAt.isNull());

        return booleanBuilder;
    }

    private boolean isEmpty(String target) {
        return target == null || target.isBlank();
    }
}
