package com.fasttime.domain.member.dto.response;

import com.fasttime.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefreshResponse {

    private Long memberId;
    private String email;
    private String nickname;
    private String image;

    @Builder
    public RefreshResponse(Long memberId, String email, String nickname, String image) {
        this.memberId = memberId;
        this.email = email;
        this.nickname = nickname;
        this.image = image;
    }

    public static RefreshResponse of(Member member) {
        return RefreshResponse.builder()
            .memberId(member.getId())
            .email(member.getEmail())
            .nickname(member.getNickname())
            .image(member.getImage())
            .build();
    }
}
