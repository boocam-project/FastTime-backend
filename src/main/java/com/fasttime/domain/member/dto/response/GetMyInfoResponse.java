package com.fasttime.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetMyInfoResponse {

    private String nickname;
    private String image;
    private String email;
    private boolean campCrtfc;
    private String bootcampName;
}
