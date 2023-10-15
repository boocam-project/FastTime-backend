package com.fasttime.domain.record.dto;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.post.entity.Post;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RecordDTO {

    private Long id;
    private Member member;
    private Post post;
    private boolean isLike;
}
