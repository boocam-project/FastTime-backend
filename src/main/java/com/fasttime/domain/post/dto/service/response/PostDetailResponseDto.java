package com.fasttime.domain.post.dto.service.response;

import com.fasttime.domain.post.entity.Post;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostDetailResponseDto {

    private final Long id;
    private final String title;
    private final String content;
    private final String nickname;
    private final boolean anonymity;
    private final int likeCount;
    private final int hateCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime lastModifiedAt;

    @Builder
    private PostDetailResponseDto(Long id, String title, String content, String nickname, boolean anonymity,
        int likeCount, int hateCount, LocalDateTime createdAt, LocalDateTime lastModifiedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.nickname = nickname;
        this.anonymity = anonymity;
        this.likeCount = likeCount;
        this.hateCount = hateCount;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
    }

    public static PostDetailResponseDto entityToDto(Post post) {
        return PostDetailResponseDto.builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .nickname(post.getMember().getNickname())
            .anonymity(post.isAnonymity())
            .likeCount(post.getLikeCount())
            .hateCount(post.getHateCount())
            .createdAt(post.getCreatedAt())
            .lastModifiedAt(post.getUpdatedAt())
            .build();
    }

}
