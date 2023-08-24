package com.fasttime.domain.member.dto.request;


import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginRequestDTO {

    @NotBlank
    String email;

    @NotBlank
    String password;


}
