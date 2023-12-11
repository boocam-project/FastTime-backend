package com.fasttime.domain.memberArticleLike.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberArticleLikeDTO {

    private Long id;
    private Long memberId;
    private Long postId;
    private Boolean isLike;
}
