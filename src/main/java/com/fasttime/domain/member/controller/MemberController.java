package com.fasttime.domain.member.controller;

import com.fasttime.domain.member.dto.MemberDto;
import com.fasttime.domain.member.dto.request.LoginRequestDTO;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.member.request.EditRequest;
import com.fasttime.domain.member.request.RePasswordRequest;
import com.fasttime.domain.member.response.EditResponse;
import com.fasttime.domain.member.response.MemberResponse;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.global.exception.ErrorCode;
import com.fasttime.global.util.ResponseDTO;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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


    @PostMapping("/api/v1/join")
    public ResponseEntity<ResponseDTO<?>> join(@Valid @RequestBody MemberDto memberDto) {
        ResponseDTO<Object> response = memberService.registerOrRecoverMember(memberDto);
        return ResponseEntity.status(HttpStatus.valueOf(response.getCode())).body(response);
    }

    @PutMapping("/api/v1/retouch-member")
    public ResponseEntity<ResponseDTO<EditResponse>> updateMember(
        @Valid @RequestBody EditRequest editRequest,
        HttpSession session) {

        return memberService.updateMemberInfo(editRequest, session)
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
    public ResponseEntity<ResponseDTO<Object>> deleteMember(HttpSession httpSession) {
        Long memberId = (Long) httpSession.getAttribute("MEMBER");
        if (memberId != null) {
            try {
                Member member = memberService.getMember(memberId);
                memberService.softDeleteMember(member);
                httpSession.invalidate();

                return ResponseEntity.ok(ResponseDTO.res(HttpStatus.OK, "탈퇴가 완료되었습니다."));
            } catch (Exception e) {

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDTO.res(HttpStatus.INTERNAL_SERVER_ERROR,
                        "회원 탈퇴 중 오류가 발생했습니다: " + e.getMessage()));
            }
        } else {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseDTO.res(HttpStatus.UNAUTHORIZED,
                    "세션에 유효한 회원 ID가 없습니다. 로그인 상태를 확인하세요."));
        }
    }

    @GetMapping("/api/v1/mypage")
    public ResponseEntity<ResponseDTO> getMyPageInfo(HttpSession session) {
        try {

            Long memberId = (Long) session.getAttribute("MEMBER");

            if (memberId == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ResponseDTO.res(HttpStatus.FORBIDDEN, "사용자가 로그인되어 있지 않습니다."));
            }

            Member member = memberService.getMember(memberId);

            String nickname = member.getNickname();
            String email = member.getEmail();
            String profileImageUrl = member.getImage();

            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("nickname", nickname);
            userInfo.put("email", email);
            userInfo.put("profileImageUrl", profileImageUrl);

            return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDTO.res(HttpStatus.OK, "사용자 정보를 성공적으로 조회하였습니다.", userInfo));
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDTO.res(HttpStatus.INTERNAL_SERVER_ERROR, "사용자 정보 조회 중 오류가 발생했습니다."));
        }
    }


    @PostMapping("/api/v1/login")
    public ResponseEntity<ResponseDTO> logIn(@Validated @RequestBody LoginRequestDTO dto
        , HttpSession session) {

        MemberResponse response = memberService.loginMember(dto);
        session.setAttribute("MEMBER", response.getId());
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.res
            (HttpStatus.OK, "로그인이 완료되었습니다.", response));
    }

    @GetMapping("/api/v1/logout")
    public ResponseEntity<ResponseDTO> logOut(HttpSession session) {
        if (session.getAttribute("ADMIN") != null) {
            session.removeAttribute("ADMIN");
        }
        if (session.getAttribute("MEMBER") != null) {
            session.removeAttribute("MEMBER");
        }
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.res
            (HttpStatus.OK, "로그아웃이 완료되었습니다."));
    }

    @PostMapping("/api/v1/RePassword")
    public ResponseEntity<ResponseDTO> rePassword
        (@Validated @RequestBody RePasswordRequest request, HttpSession session) {

        MemberResponse response =
            memberService.rePassword(request, (Long) session.getAttribute("MEMBER"));
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.res
            (HttpStatus.OK, "패스워드 재설정이 완료되었습니다", response));
    }
}

