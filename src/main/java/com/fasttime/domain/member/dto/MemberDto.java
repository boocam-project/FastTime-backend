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

public class MemberDto {
    // 회원가입 요청 DTO

    private String email; // 이메일

    private String password; // 비밀번호

    private String nickname; // 닉네임

    private String image; // 프로필사진url


    // email, password, nickname을 받는 생성자
    public MemberDto(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    // email, password, image를 받는 생성자
    public MemberDto(String nickname, String password, String email, String image) {
        this.email = email;
        this.password = password;
        this.image = image;
        this.nickname = nickname;
    }




}
