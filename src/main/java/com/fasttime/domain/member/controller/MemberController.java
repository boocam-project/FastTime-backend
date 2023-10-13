package com.fasttime.domain.member.controller;

import com.fasttime.domain.member.dto.request.LoginRequestDTO;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.dto.MemberDto;


import com.fasttime.domain.member.exception.UserNotFoundException;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.member.request.EditRequest;
import com.fasttime.domain.member.request.RePasswordRequest;
import com.fasttime.domain.member.response.EditResponse;
import com.fasttime.domain.member.response.MemberResponse;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.global.util.ResponseDTO;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    private final MemberRepository memberRepository;


    @PostMapping("/v1/join")
    public ResponseEntity<String> join(@Valid @RequestBody MemberDto memberDto) {
        try {
            if (memberService.isEmailExistsInFcmember(memberDto.getEmail())) {
                if (memberService.isEmailExistsInMember(memberDto.getEmail())) {
                    return ResponseEntity.badRequest().body("이미 가입된 회원입니다.");
                } else if (memberService.checkDuplicateNickname(memberDto.getNickname())) {
                    return ResponseEntity.badRequest().body("이미 사용 중인 닉네임 입니다.");
                }

                memberService.save(memberDto);
                return ResponseEntity.ok("가입 성공!");
            } else {
                return ResponseEntity.badRequest().body("FastCampus에 등록된 이메일이 아닙니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("회원가입 실패 " + e.getMessage());
        }
    }

    @PutMapping("v1/retouch-member") // 회원 정보 수정
    public ResponseEntity<EditResponse> updateMember(@RequestBody EditRequest editRequest,
        HttpSession session) {
        Member member = (Member) session.getAttribute("member");
        if (member != null) {
            // 닉네임 중복 여부 검사
            if (!member.getNickname().equals(editRequest.getNickname()) &&
                memberService.checkDuplicateNickname(editRequest.getNickname())) {
                return ResponseEntity.badRequest().body(new EditResponse("중복된 닉네임입니다."));
            }

            // 이미지 업데이트
            member.setImage(editRequest.getImage());

            // 업데이트된 Member 엔티티를 데이터베이스에 저장
            memberRepository.save(member);

            EditResponse memberResponse = new EditResponse(member);
            return ResponseEntity.ok(memberResponse);
        }

        EditResponse e = new EditResponse("로그인 상태가 아닙니다.");
        return ResponseEntity.badRequest().body(e);
    }

    //패스워드 수정은 security적용 후 구현 가능합니다.


    @DeleteMapping("v1/delete") // 회원탈퇴 (soft delete 적용)
    public ResponseEntity<String> deleteMember(HttpSession httpSession) {
        Member member = (Member) httpSession.getAttribute("member");
        try {
            memberService.softDeleteMember(member); // 소프트 삭제 메소드 호출

            httpSession.invalidate(); // 세션 무효화
            return ResponseEntity.ok("탈퇴가 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("알 수 없는 오류가 발생했습니다.");
        }
    }

    @GetMapping("/api/v1/mypage")
    public ResponseEntity<ResponseDTO> getMyPageInfo(HttpSession session) {
        Member member = (Member) session.getAttribute("MEMBER");

        try {
            MemberDto memberDto = memberService.getMyPageInfo(member);
            return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDTO.res(HttpStatus.OK, "사용자 정보를 성공적으로 조회하였습니다.", memberDto));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseDTO.res(HttpStatus.NOT_FOUND, e.getMessage()));
        }
    }


    @PostMapping("/api/v1/login")
    public ResponseEntity<ResponseDTO> logIn(@Validated @RequestBody LoginRequestDTO dto
        , BindingResult bindingResult, HttpSession session)
        throws UserNotFoundException, BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
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
        (@Validated @RequestBody RePasswordRequest request, BindingResult bindingResult)
        throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        MemberResponse response = memberService.rePassword(request);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.res
            (HttpStatus.OK, "패스워드 재설정이 완료되었습니다", response));
    }
}

