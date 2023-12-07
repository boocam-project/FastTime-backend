package com.fasttime.domain.member.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RePasswordRequest {
    @NotBlank
    String password;
    @NotBlank
    String rePassword;
}
