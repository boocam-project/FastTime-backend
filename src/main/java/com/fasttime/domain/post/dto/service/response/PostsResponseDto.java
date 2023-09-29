package com.fasttime.domain.post.dto.service.response;

import com.fasttime.domain.post.entity.Post;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostsResponseDto {

    private final Long id;
    private final String title;
    private final boolean anonymity;
    private final int commentCounts;
    private final int likeCount;
    private final int hateCount;

    @Builder
    private PostsResponseDto(Long id, String title, boolean anonymity, int commentCounts, int likeCount,
        int hateCount) {
        this.id = id;
        this.title = title;
        this.anonymity = anonymity;
        this.commentCounts = commentCounts;
        this.likeCount = likeCount;
        this.hateCount = hateCount;
    }

    public static PostsResponseDto entityToDto(Post post) {
        return PostsResponseDto.builder()
            .id(post.getId())
            .title(post.getTitle())
            .anonymity(post.isAnonymity())
            .likeCount(post.getLikeCount())
            .hateCount(post.getHateCount())
            .build();
    }
}
