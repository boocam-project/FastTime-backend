package com.fasttime.domain.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateMemberDTO {

    @NotNull
    @Email(message = "이메일 형식이 유효하지 않습니다.")
    private String email;

    @NotNull
    private String password;

    @NotNull
    private String nickname;

}
