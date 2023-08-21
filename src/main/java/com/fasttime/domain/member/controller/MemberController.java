package com.fasttime.domain.member.controller;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.dto.MemberDto;


import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;



    private final MemberRepository memberRepository;

    @PostMapping("/users/new-user") // 회원가입
    public ResponseEntity<String> join(@Valid @RequestBody MemberDto userDto) { // @Valid 어노테이션 추가
        try {
            memberService.save(userDto);
            return ResponseEntity.ok("Join success"); // 메시지 명시
        } catch (DuplicateKeyException e) { // 중복된 이메일인 경우
            return ResponseEntity.badRequest().body("Join failed: Email already exists");
        } catch (Exception e) { // 그 외의 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 실패: " + e.getMessage());
        }

    }



    @GetMapping("/checkDuplicate/{nickname}") // 닉네임 중복 확인
    public boolean checkDuplicateNickname(@PathVariable String nickname) {
        return memberService.checkDuplicateNickname(nickname);
    } // 중복이면 true, 아니면 false


/*    @DeleteMapping("/delete") // 회원탈퇴
    public ResponseEntity<String> deleteUser(HttpSession httpSession) {
        Member member = (Member) httpSession.getAttribute("member");
        try {
            memberService.deleteUser(member.getId());
            httpSession.invalidate(); // 세션 무효화
            return ResponseEntity.ok("탈퇴가 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("알 수 없는 오류가 발생했습니다.");
        }
    }*/
//로그인 구현하면 후에 구현











}


