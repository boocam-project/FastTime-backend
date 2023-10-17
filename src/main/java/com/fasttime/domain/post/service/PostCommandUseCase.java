package com.fasttime.domain.post.service;

import com.fasttime.domain.post.dto.service.response.PostDetailResponseDto;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;

public interface PostCommandUseCase {

    PostDetailResponseDto writePost(PostCreateServiceDto serviceDto);

    PostDetailResponseDto updatePost(PostUpdateServiceDto serviceDto);

    void deletePost(PostDeleteServiceDto serviceDto);

    @Getter
    class PostCreateServiceDto {

        @NotNull
        private final Long memberId;

        @NotBlank
        private final String title;

        @NotBlank
        private final String content;

        private final boolean anonymity;

        public PostCreateServiceDto(Long memberId, String title, String content, boolean anonymity) {
            this.memberId = memberId;
            this.title = title;
            this.content = content;
            this.anonymity = anonymity;
        }
    }

    @Getter
    class PostUpdateServiceDto {

        private final Long postId;
        private final Long memberId;
        private final String title;
        private final String content;

        public PostUpdateServiceDto(Long postId, Long memberId, String title, String content) {
            this.postId = postId;
            this.memberId = memberId;
            this.title = title;
            this.content = content;
        }
    }

    @Getter
    class PostDeleteServiceDto {

        private final Long postId;
        private final Long memberId;
        private final LocalDateTime deletedAt;

        public PostDeleteServiceDto(Long postId, Long memberId, LocalDateTime deletedAt) {
            this.postId = postId;
            this.memberId = memberId;
            this.deletedAt = deletedAt;
        }
    }

}
