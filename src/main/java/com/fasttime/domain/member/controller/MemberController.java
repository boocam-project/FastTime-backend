package com.fasttime.domain.member.controller;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.dto.MemberDto;


import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
                    return ResponseEntity.badRequest().body("가입 실패: 해당 이메일은 이미 회원 목록에 존재합니다");
                } else if (memberService.checkDuplicateNickname(memberDto.getNickname())) {
                    return ResponseEntity.badRequest().body("가입 실패: 중복된 닉네임.");
                }

                memberService.save(memberDto);
                return ResponseEntity.ok("가입 성공");
            } else {
                return ResponseEntity.badRequest().body("가입 실패: 수강생 이메일이 아닙니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("회원가입 실패: " + e.getMessage());
        }
    }







/*    @DeleteMapping("v1/delete") // 회원탈퇴
    public ResponseEntity<String> deleteUser(HttpSession httpSession) {
        Member member = (Member) httpSession.getAttribute("member");
        try {
            memberService.deleteMember(member.getId());
            httpSession.invalidate(); // 세션 무효화
            return ResponseEntity.ok("탈퇴가 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("알 수 없는 오류가 발생했습니다.");
        }
    }*/
//로그인 구현하면 후에 구현


}

