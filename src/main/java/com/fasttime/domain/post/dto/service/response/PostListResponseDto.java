package com.fasttime.domain.post.dto.service.response;

import com.fasttime.domain.post.entity.Post;
import lombok.Getter;

@Getter
public class PostListResponseDto {

    private final Long id;
    private final String title;
    private final boolean anonymity;
    private final int likeCount;
    private final int hateCount;

    private PostListResponseDto(Long id, String title, boolean anonymity, int likeCount,
        int hateCount) {
        this.id = id;
        this.title = title;
        this.anonymity = anonymity;
        this.likeCount = likeCount;
        this.hateCount = hateCount;
    }

    public static PostListResponseDto of(Post post) {

        return new PostListResponseDto(post.getId(),
            post.getTitle(),
            post.isAnonymity(),
            post.getLikeCount(),
            post.getHateCount());
    }
}
