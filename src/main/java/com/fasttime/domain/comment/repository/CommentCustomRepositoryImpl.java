package com.fasttime.domain.comment.repository;

import static com.fasttime.domain.comment.entity.QComment.comment;

import com.fasttime.domain.comment.dto.request.GetCommentsRequestDTO;
import com.fasttime.domain.comment.entity.Comment;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class CommentCustomRepositoryImpl {

    private final JPAQueryFactory jpaQueryFactory;

    public CommentCustomRepositoryImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    public List<Comment> findAllBySearchCondition(GetCommentsRequestDTO getCommentsRequestDTO) {
        return jpaQueryFactory.selectFrom(comment).leftJoin(comment.parentComment).fetchJoin()
            .where(createSearchConditionsBuilder(getCommentsRequestDTO))
            .offset((long) getCommentsRequestDTO.getPage() * getCommentsRequestDTO.getPageSize())
            .limit(getCommentsRequestDTO.getPageSize()).orderBy(comment.createdAt.asc()).fetch();
    }

    private BooleanBuilder createSearchConditionsBuilder(
        GetCommentsRequestDTO getCommentsRequestDTO) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        boolean isFindAllByArticleId = getCommentsRequestDTO.getArticleId() != null;
        boolean isFindAllByMemberId = getCommentsRequestDTO.getMemberId() != null;
        boolean isFindAllByParentCommentId = getCommentsRequestDTO.getParentCommentId() != null;
        if (isFindAllByArticleId) {
            booleanBuilder.and(comment.article.id.eq(getCommentsRequestDTO.getArticleId()));
            booleanBuilder.and(comment.parentComment.isNull());
        }
        if (isFindAllByMemberId) {
            booleanBuilder.and(comment.member.id.eq(getCommentsRequestDTO.getMemberId()));
        }
        if (isFindAllByParentCommentId) {
            booleanBuilder.and(
                comment.parentComment.id.eq(getCommentsRequestDTO.getParentCommentId()));
        }
        booleanBuilder.and(comment.deletedAt.isNull());
        return booleanBuilder;
    }
}
