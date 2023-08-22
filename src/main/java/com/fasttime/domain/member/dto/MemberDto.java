package com.fasttime.domain.member.dto;

import com.fasttime.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    // 회원가입 요청 DTO

    private String email; // 이메일

    private String password; // 비밀번호

    private String nickname; // 닉네임

}
