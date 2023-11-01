package com.fasttime.domain.article.repository;


import static com.fasttime.domain.article.entity.QArticle.article;

import com.fasttime.domain.article.service.ArticleQueryUseCase.ArticleSearchCondition;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;

public class ArticleRepositoryImpl implements ArticleRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public ArticleRepositoryImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<ArticleQueryResponse> search(ArticleSearchCondition articleSearchCondition) {
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
            .where(createSearchConditionBuilder(articleSearchCondition))
            .offset((long) articleSearchCondition.getPage() * articleSearchCondition.getPageSize())
            .limit(articleSearchCondition.getPageSize())
            .orderBy(article.createdAt.desc())
            .fetch();
    }

    private BooleanBuilder createSearchConditionBuilder(ArticleSearchCondition searchCondition) {
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
