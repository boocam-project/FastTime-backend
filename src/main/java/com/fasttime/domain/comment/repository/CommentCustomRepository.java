package com.fasttime.domain.comment.repository;

import com.fasttime.domain.comment.dto.request.GetCommentsRequestDTO;
import com.fasttime.domain.comment.entity.Comment;
import java.util.List;

public interface CommentCustomRepository {

    List<Comment> findAllBySearchCondition(GetCommentsRequestDTO getCommentsRequestDTO);
}
