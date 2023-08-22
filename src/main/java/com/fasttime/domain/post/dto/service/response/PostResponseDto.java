package com.fasttime.domain.post.dto.service.response;

import com.fasttime.domain.post.entity.Post;
import lombok.Getter;

@Getter
public class PostResponseDto {

    private final Long id;
    private final String title;
    private final String content;
    private final boolean anonymity;
    private final int likeCount;
    private final int hateCount;

    private PostResponseDto(Long id, String title, String content, boolean anonymity, int likeCount,
        int hateCount) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.anonymity = anonymity;
        this.likeCount = likeCount;
        this.hateCount = hateCount;
    }

    public static PostResponseDto of(Post post) {

        return new PostResponseDto(post.getId(),
            post.getTitle(),
            post.getContent(),
            post.isAnonymity(),
            post.getLikeCount(),
            post.getHateCount());
    }
}
