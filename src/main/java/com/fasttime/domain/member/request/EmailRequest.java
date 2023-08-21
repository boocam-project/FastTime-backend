package com.fasttime.domain.member.request;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailRequest { // 이메일 요청
    private String email;

}