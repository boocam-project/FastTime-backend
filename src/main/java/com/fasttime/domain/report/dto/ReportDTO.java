package com.fasttime.domain.report.dto;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.post.entity.Post;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReportDTO {

    private Long id;
    private Long memberId;
    private Long postId;
}
