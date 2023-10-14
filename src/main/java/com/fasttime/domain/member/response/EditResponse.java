package com.fasttime.domain.member.response;

import com.fasttime.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EditResponse {

    private String email;
    private String nickname;
    private String image;


    private String message;

    public EditResponse(Member member) {
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.image = member.getImage();

    }

    public EditResponse(String message) {
        this.message = message;
    }

}