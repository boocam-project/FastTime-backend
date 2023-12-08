package com.fasttime.domain.member.controller;

import com.fasttime.domain.member.dto.MemberDto;
import com.fasttime.domain.member.dto.request.LoginRequestDTO;
import com.fasttime.domain.member.dto.request.MyPageInfoDTO;
import com.fasttime.domain.member.dto.request.RefreshRequestDto;
import com.fasttime.domain.member.dto.response.LogInResponseDto;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.member.request.EditRequest;
import com.fasttime.domain.member.request.RePasswordRequest;
import com.fasttime.domain.member.response.EditResponse;
import com.fasttime.domain.member.response.MemberResponse;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.global.exception.ErrorCode;
import com.fasttime.global.util.ResponseDTO;
import com.fasttime.global.util.SecurityUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    private final MemberRepository memberRepository;

    private final SecurityUtil securityUtil;

    @PostMapping("/api/v1/join")
    public ResponseEntity<ResponseDTO<?>> join(@Valid @RequestBody MemberDto memberDto) {
        ResponseDTO<Object> response = memberService.registerOrRecoverMember(memberDto);
        return ResponseEntity.status(HttpStatus.valueOf(response.getCode())).body(response);
    }

    @PutMapping("/api/v1/retouch-member")
    public ResponseEntity<ResponseDTO<EditResponse>> updateMember(
        @Valid @RequestBody EditRequest editRequest) {

        return memberService.updateMemberInfo(editRequest, securityUtil.getCurrentMemberId())
            .map(updatedMember -> ResponseEntity.ok(
                ResponseDTO.<EditResponse>res(ErrorCode.MEMBER_UPDATE_SUCCESS.getHttpStatus(),
                    ErrorCode.MEMBER_UPDATE_SUCCESS.getMessage(),
                    new EditResponse(updatedMember.getEmail(),
                        updatedMember.getNickname(),
                        updatedMember.getImage()))
            ))
            .orElseGet(() -> ResponseEntity
                .status(ErrorCode.MEMBER_NOT_FOUND.getHttpStatus())
                .body(ResponseDTO.<EditResponse>res(ErrorCode.MEMBER_NOT_FOUND.getHttpStatus(),
                    ErrorCode.MEMBER_NOT_FOUND.getMessage(),
                    null))
            );
    }


    @DeleteMapping("/api/v1/delete")
    public ResponseEntity<ResponseDTO<Object>> deleteMember() {

        try {
            Member member = memberService.getMember(securityUtil.getCurrentMemberId());
            memberService.softDeleteMember(member);
            return ResponseEntity.ok(ResponseDTO.res(HttpStatus.OK, "탈퇴가 완료되었습니다."));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDTO.res(HttpStatus.INTERNAL_SERVER_ERROR,
                        "회원 탈퇴 중 오류가 발생했습니다: " + e.getMessage()));
        }

    }

    @GetMapping("/api/v1/mypage")
    public ResponseEntity<ResponseDTO> getMyPageInfo() {

        MyPageInfoDTO myPageInfoDto = memberService
            .getMyPageInfoById(securityUtil.getCurrentMemberId());
        return ResponseEntity.ok(
            ResponseDTO.res(HttpStatus.OK, ErrorCode.MY_PAGE_RETRIEVED_SUCCESS.getMessage(),
                myPageInfoDto));
    }


    @PostMapping("/api/v2/login")
    public ResponseEntity<ResponseDTO<LogInResponseDto>> logIn
        (@Validated @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.res
            (HttpStatus.OK, "로그인이 완료되었습니다.",memberService.loginMember(dto)));
    }

    @PostMapping("/api/v2/refresh")
    public ResponseEntity<ResponseDTO<LogInResponseDto>> refresh(
        @Validated @RequestBody RefreshRequestDto dto) {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.res
            (HttpStatus.OK, "성공적으로 토큰을 재발급 했습니다.",memberService.refresh(dto)));

    }

    @PostMapping("/api/v1/RePassword")
    public ResponseEntity<ResponseDTO<MemberResponse>> rePassword
        (@Validated @RequestBody RePasswordRequest request) {

        MemberResponse response =
            memberService.rePassword(request, securityUtil.getCurrentMemberId());
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.res
            (HttpStatus.OK, "패스워드 재설정이 완료되었습니다", response));
    }

}

