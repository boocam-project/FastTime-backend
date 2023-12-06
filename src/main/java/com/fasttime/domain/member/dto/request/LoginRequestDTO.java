package com.fasttime.domain.member.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginRequestDTO {

    @NotBlank
    private String email;

    @NotBlank
    private String password;


}
