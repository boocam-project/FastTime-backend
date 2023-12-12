package com.fasttime.domain.member.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class LogInResponseDto {
    private MemberResponseDto member;
    private TokenResponseDto token;

    @Builder
    public LogInResponseDto(MemberResponseDto member, TokenResponseDto token) {
        this.member = member;
        this.token = token;
    }

    public static LogInResponseDto of(MemberResponseDto member, TokenResponseDto token) {
        return LogInResponseDto.builder()
            .member(member)
            .token(token)
            .build();
    }

}
