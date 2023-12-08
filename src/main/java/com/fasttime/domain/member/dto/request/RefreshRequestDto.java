package com.fasttime.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefreshRequestDto {
    @NotBlank(message = "Access Token 을 입력하세요.")
    private String accessToken;
    @NotBlank(message = "Refresh Token 을 입력하세요.")
    private String refreshToken;

    @Builder
    public RefreshRequestDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }


}
