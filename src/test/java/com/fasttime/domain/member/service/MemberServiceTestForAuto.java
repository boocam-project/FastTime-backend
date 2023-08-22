package com.fasttime.domain.member.service;

import com.fasttime.domain.member.dto.request.LoginRequestDTO;
import com.fasttime.domain.member.dto.MemberDto;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.exception.UserNotFoundException;
import com.fasttime.domain.member.repository.MemberRepository;
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
            MemberDto memberDto = memberService.loginMember(dto);
            Member byEmail = memberRepository.findByEmail(memberDto.getEmail()).get();
            //then
            Assertions.assertThat(memberDto.getNickname()).isEqualTo(byEmail.getNickname());
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

}
