package com.fasttime.domain.member.request;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RePasswordRequest {
    String email;
    @NotBlank
    String password;
    @NotBlank
    String rePassword;
}
