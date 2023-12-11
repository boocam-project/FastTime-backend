package com.fasttime.domain.article.repository;


import static com.fasttime.domain.article.entity.QArticle.article;

import com.fasttime.domain.article.service.usecase.ArticleQueryUseCase.ArticlesSearchRequestServiceDto;
import com.querydsl.core.BooleanBuilder;
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
            .offset((long) searchCondition.page() * searchCondition.pageSize())
            .limit(searchCondition.pageSize())
            .orderBy(article.createdAt.desc())
            .fetch();
    }

    private BooleanBuilder createSearchConditionBuilder(
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

    private boolean isEmpty(String target) {
        return target == null || target.isBlank();
    }
}
