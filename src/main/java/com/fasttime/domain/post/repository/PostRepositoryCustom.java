package com.fasttime.domain.post.repository;

import com.fasttime.domain.post.service.PostQueryUseCase.PostSearchCondition;
import java.util.List;
import lombok.Getter;

public interface PostRepositoryCustom {

    List<PostsRepositoryResponseDto> search(PostSearchCondition postSearchCondition);

    @Getter
    class PostsRepositoryResponseDto {

        private Long id;
        private Long memberId;
        private String nickname;
        private String title;
        private boolean anonymity;
        private int likeCount;
        private int hateCount;
    }
}
