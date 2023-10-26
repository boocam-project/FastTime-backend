package com.fasttime.domain.post.repository;


import static com.fasttime.domain.post.entity.QPost.post;

import com.fasttime.domain.post.service.PostQueryUseCase.PostSearchCondition;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;

public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public PostRepositoryImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<PostsRepositoryResponseDto> search(PostSearchCondition postSearchCondition) {
        return jpaQueryFactory
            .select(Projections.fields(PostsRepositoryResponseDto.class,
                post.id,
                post.member.id.as("memberId"),
                post.member.nickname,
                post.title,
                post.anonymity,
                post.comments.size().as("commentCount"),
                post.likeCount,
                post.hateCount,
                post.createdAt,
                post.updatedAt,
                post.deletedAt
            ))
            .from(post)
            .where(createSearchConditionBuilder(postSearchCondition))
            .offset((long) postSearchCondition.getPage() * postSearchCondition.getPageSize())
            .limit(postSearchCondition.getPageSize())
            .orderBy(post.createdAt.desc())
            .fetch();
    }

    private BooleanBuilder createSearchConditionBuilder(PostSearchCondition searchCondition) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (!isEmpty(searchCondition.getTitle())) {
            booleanBuilder.and(post.title.contains(searchCondition.getTitle()));
        }

        if (!isEmpty(searchCondition.getNickname())) {
            booleanBuilder.and(post.member.nickname.contains(searchCondition.getNickname()));
        }

        if (searchCondition.getLikeCount() > 0) {
            booleanBuilder.and(post.likeCount.gt(searchCondition.getLikeCount()));
        }

        booleanBuilder.and(post.deletedAt.isNull());

        return booleanBuilder;
    }

    private boolean isEmpty(String target) {
        return target == null || target.isBlank();
    }
}
