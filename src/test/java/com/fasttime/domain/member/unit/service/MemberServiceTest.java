package com.fasttime.domain.member.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.fasttime.domain.member.dto.request.LoginRequestDTO;
import com.fasttime.domain.member.dto.request.RePasswordRequest;
import com.fasttime.domain.member.dto.response.LogInResponseDto;
import com.fasttime.domain.member.dto.response.MemberResponse;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.exception.MemberNotMatchInfoException;
import com.fasttime.domain.member.exception.MemberNotMatchRePasswordException;
import com.fasttime.domain.member.exception.MemberSoftDeletedException;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.member.service.MemberService;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @DisplayName("loginMember()는")
    @Nested
    class Login {

        @DisplayName("로그인을 성공한다.")
        @Test
        void _willSuccess() {
            //given
            Member member = Member.builder()
                .id(1L)
                .email("testEmail")
                .password("testPassword")
                .nickname("땅땅띠라랑").build();
            LoginRequestDTO dto = new LoginRequestDTO("testEmail", "testPassword");

            given(memberRepository.findByEmail(any(String.class))).willReturn(Optional.of(member));
            given(passwordEncoder.matches(any(String.class),any(String.class))).willReturn(true);

            //when
            LogInResponseDto logInResponseDto = memberService.loginMember(dto);

            //then
            assertThat(logInResponseDto).extracting("id",  "nickname")
                .containsExactly(1L, member.getNickname());

        }


        @DisplayName("로그인을 등록되지않는 이메일로 인해 실패한다.")
        @Test
        void Email_willFail() {
            //given
            LoginRequestDTO dto = new LoginRequestDTO("email", "testPassword");

            given(memberRepository.findByEmail(any(String.class))).willReturn(Optional.empty());

            //when,then
            assertThatThrownBy(() -> memberService.loginMember(dto))
                .isInstanceOf(MemberNotMatchInfoException.class)
               .hasMessage("아이디 또는 비밀번호가 일치하지 않습니다.");


        }

        @DisplayName("로그인을 비밀번호가 달라 실패한다.")
        @Test
        void password_willFail() throws Exception {
            //given
            Member member = Member.builder()
                .id(1L)
                .email("testEmail")
                .password("testPassword")
                .nickname("땅땅띠라랑").build();

            LoginRequestDTO dto = new LoginRequestDTO("testEmail", "testPassword");

            given(memberRepository.findByEmail(any(String.class))).willReturn(Optional.of(member));
            given(passwordEncoder.matches(any(String.class),any(String.class))).willReturn(false);

            //when,then
            assertThatThrownBy(() -> memberService.loginMember(dto))
                .isInstanceOf(MemberNotMatchInfoException.class)
                .hasMessage("아이디 또는 비밀번호가 일치하지 않습니다.");

        }

        @DisplayName("로그인을 이미 탈퇴한 회원이라서 실패한다.")
        @Test
        void softDeleted_willFail() throws Exception {
            //given
            Member member = Member.builder()
                .id(1L)
                .email("testEmail")
                .password("testPassword")
                .nickname("땅땅띠라랑").build();
            member.delete(LocalDateTime.now());
            LoginRequestDTO dto = new LoginRequestDTO("testEmail", "testPassword");

            given(memberRepository.findByEmail(any(String.class))).willReturn(Optional.of(member));

            //when,then
            assertThatThrownBy(() -> memberService.loginMember(dto))
                .isInstanceOf(MemberSoftDeletedException.class)
                .hasMessage("이미 탈퇴한 회원입니다.");

        }

    }



    @DisplayName("rePassword()는")
    @Nested
    class RePassword {

        @DisplayName("비밀번호 재설정을 성공한다.")
        @Test
        void _willSuccess() throws Exception {
            //given
            Member member = Member.builder()
                .id(1L)
                .email("testEmail")
                .password("testPassword")
                .nickname("땅땅띠라랑").build();
            RePasswordRequest request = new RePasswordRequest
                ("newPassword", "newPassword");

            given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));

            //when
            MemberResponse memberResponse = memberService.rePassword(request, member.getId());

            //then
            assertThat(memberResponse).extracting("id", "nickname")
                .containsExactly(member.getId(), member.getNickname());

        }



        @DisplayName("비밀번호 재확인이 일치하지 않음으로 실패한다.")
        @Test
        void Re_willFail() throws Exception {
            //given
            RePasswordRequest request = new RePasswordRequest
                ("newPassword", "new");

            //when, then
            assertThatThrownBy(() -> memberService.rePassword(request,1L))
                .isInstanceOf(MemberNotMatchRePasswordException.class)
                .hasMessage("비밀번호 재확인이 일치하지 않습니다.");

        }
    }

}
