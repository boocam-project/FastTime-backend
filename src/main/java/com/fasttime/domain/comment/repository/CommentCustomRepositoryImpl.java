package com.fasttime.domain.comment.repository;

import static com.fasttime.domain.article.entity.QArticle.article;
import static com.fasttime.domain.comment.entity.QComment.comment;
import static com.fasttime.domain.member.entity.QMember.member;

import com.fasttime.domain.comment.dto.request.GetCommentsRequestDTO;
import com.fasttime.domain.comment.entity.Comment;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
public class CommentCustomRepositoryImpl implements CommentCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public CommentCustomRepositoryImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Comment> findAllBySearchCondition(GetCommentsRequestDTO getCommentsRequestDTO,
        Pageable pageable) {
        List<Comment> content = jpaQueryFactory
            .selectFrom(comment)
            .leftJoin(comment.article, article)
            .leftJoin(comment.member, member)
            .leftJoin(comment.parentComment)
            .fetchJoin()
            .where(createSearchConditionsBuilder(getCommentsRequestDTO))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(comment.createdAt.asc())
            .fetch();
        JPAQuery<Long> countQuery = jpaQueryFactory
            .select(comment.count())
            .from(comment)
            .leftJoin(comment.article, article)
            .leftJoin(comment.member, member)
            .leftJoin(comment.parentComment)
            .where(createSearchConditionsBuilder(getCommentsRequestDTO));
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Long countByArticleId(long articleId) {
        return jpaQueryFactory
            .select(comment.count())
            .from(comment)
            .leftJoin(comment.article, article)
            .where(comment.article.id.eq(articleId).and(comment.deletedAt.isNull()))
            .fetchOne();
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
