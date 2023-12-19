package com.fasttime.domain.member.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeleteMemberRequest { // 회원탈퇴 요청

    private String password;
}