package com.fasttime.domain.comment.repository;

import com.fasttime.domain.comment.dto.request.GetCommentsRequestDTO;
import com.fasttime.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentCustomRepository {

    Page<Comment> findAllBySearchCondition(GetCommentsRequestDTO getCommentsRequestDTO,
        Pageable pageable);

    Long countByArticleId(long articleId);
}
