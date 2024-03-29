package com.fasttime.domain.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateAdminRequest {

    private String email;
    private String password;

}
