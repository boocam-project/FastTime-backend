package com.fasttime.domain.member.unit.service;

import com.fasttime.domain.member.dto.request.LoginRequestDTO;
import com.fasttime.domain.member.dto.MemberDto;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.exception.UserNotFoundException;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.member.request.RePasswordRequest;
import com.fasttime.domain.member.response.MemberResponse;
import com.fasttime.domain.member.service.MemberService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
public class MemberServiceTestForAuto {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;


    @DisplayName("로그인을")
    @Nested
    class Login {

        @BeforeEach
        void testMember() {
            MemberDto memberDto = new MemberDto("testEmail",
                "testPassword", "testNickname");
            memberService.save(memberDto);
        }

        @DisplayName("성공한다.")
        @Test
        void _willSuccess() throws UserNotFoundException {
            //given
            LoginRequestDTO dto = new LoginRequestDTO("testEmail", "testPassword");
            //when
            MemberResponse response = memberService.loginMember(dto);
            Member byEmail = memberRepository.findByNickname(response.getNickname()).get();
            //then
            Assertions.assertThat(response.getNickname()).isEqualTo(byEmail.getNickname());
        }

        @DisplayName("아이디가 맞지 않아 실패한다.")
        @Test
        void UserNotFound_willFail() {
            //given
            LoginRequestDTO dto = new LoginRequestDTO("email", "testPassword");
            //when,then
            Assertions.assertThatThrownBy(() -> memberService.loginMember(dto))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with email: email");
        }

        @DisplayName("비밀번호가 맞지 않아 실패한다.")
        @Test
        void BadCredentials_willFail() {
            //given
            LoginRequestDTO dto = new LoginRequestDTO("testEmail", "Password");
            //when,then
            Assertions.assertThatThrownBy(() -> memberService.loginMember(dto))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Not match password!");
        }
    }

    @DisplayName("비밀번호 재설정을")
    @Nested
    class RePasswordTest{
        @BeforeEach
        void testMember() {
            MemberDto memberDto = new MemberDto("testEmail",
                "testPassword", "testNickname");
            memberService.save(memberDto);
        }

        @DisplayName("성공한다.")
        @Test
        void _willSuccess(){
            //given
            RePasswordRequest request = new RePasswordRequest
                ("newPassword", "newPassword");
            //when
            Member loginMember = memberRepository.findByNickname("testNickname").get();
            MemberResponse response = memberService.rePassword(request, loginMember.getId());
            Member member = memberRepository.findByNickname(response.getNickname()).get();
            //then
            Assertions.assertThat(response.getNickname()).isEqualTo(member.getNickname());
        }
        @DisplayName("비밀번호 재확인으로 인해 실패한다.")
        @Test
        void Re_willFail(){
            //given
            RePasswordRequest request = new RePasswordRequest
                ("newPassword", "new");
            //when

            //then
            Assertions.assertThatThrownBy(() -> memberService.rePassword(request,1L))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Not Match RePassword!");

        }
    }

}
