package com.fasttime.domain.post.service;

import com.fasttime.domain.post.dto.service.response.PostDetailResponseDto;
import com.fasttime.domain.post.dto.service.response.PostsResponseDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

public interface PostQueryUseCase {

    PostDetailResponseDto findById(Long id);

    List<PostsResponseDto> searchPost(PostSearchCondition postSearchCondition);

    @Getter
    class PostSearchCondition {

        private final String nickname;
        private final String title;
        private final int likeCount;
        private final int pageSize;
        private final int page;

        @Builder
        private PostSearchCondition(String nickname, String title, int likeCount, int pageSize,
            int page) {
            this.nickname = nickname;
            this.title = title;
            this.likeCount = likeCount;
            this.pageSize = pageSize;
            this.page = page;
        }
    }
}
