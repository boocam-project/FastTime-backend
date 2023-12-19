package com.fasttime.domain.member.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class LoginResponse {
    private RefreshResponse member;
    private TokenResponse token;

    @Builder
    public LoginResponse(RefreshResponse member, TokenResponse token) {
        this.member = member;
        this.token = token;
    }

    public static LoginResponse of(RefreshResponse member, TokenResponse token) {
        return LoginResponse.builder()
            .member(member)
            .token(token)
            .build();
    }

}
