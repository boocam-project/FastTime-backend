package com.fasttime.domain.member.dto.response;

import com.fasttime.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberResponseDto {

    private Long memberId;
    private String email;
    private String nickname;
    private String image;

    @Builder
    public MemberResponseDto(Long memberId, String email, String nickname, String image) {
        this.memberId = memberId;
        this.email = email;
        this.nickname = nickname;
        this.image = image;
    }

    public static MemberResponseDto of(Member member) {
        return MemberResponseDto.builder()
            .memberId(member.getId())
            .email(member.getEmail())
            .nickname(member.getNickname())
            .image(member.getImage())
            .build();
    }
}
