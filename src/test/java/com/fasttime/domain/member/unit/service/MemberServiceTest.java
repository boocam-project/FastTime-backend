package com.fasttime.domain.member.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasttime.domain.member.dto.request.LoginRequestDTO;
import com.fasttime.domain.member.dto.request.RePasswordRequest;
import com.fasttime.domain.member.dto.request.RefreshRequestDto;
import com.fasttime.domain.member.dto.response.LogInResponseDto;
import com.fasttime.domain.member.dto.response.MemberResponse;
import com.fasttime.domain.member.dto.response.TokenResponseDto;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.entity.RefreshToken;
import com.fasttime.domain.member.entity.Role;
import com.fasttime.domain.member.exception.InvalidRefreshTokenException;
import com.fasttime.domain.member.exception.MemberNotMatchInfoException;
import com.fasttime.domain.member.exception.MemberNotMatchRePasswordException;
import com.fasttime.domain.member.exception.MemberSoftDeletedException;
import com.fasttime.domain.member.exception.UnmatchedMemberException;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.member.repository.RefreshTokenRepository;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.global.jwt.JwtProvider;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
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

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtProvider jwtTokenProvider;


    @DisplayName("loginMember()는")
    @Nested
    class Login {

        @Test
        @DisplayName("로그인을 할 수 있다.")
        void _willSuccess() {
            // given
            LoginRequestDTO signInRequestDto = LoginRequestDTO.builder()
                .email("test@mail.com")
                .password("qwer1234$$")
                .build();
            Member member = Member.builder()
                .id(1L)
                .email("test@mail.com")
                .nickname("test")
                .password(passwordEncoder.encode("qwer1234$$"))
                .image(
                    "https://fastly.picsum.photos/id/866/200/300.jpg?hmac=rcadCENKh4rD6MAp6V_ma-AyWv641M4iiOpe1RyFHeI")
                .role(Role.ROLE_USER)
                .build();
            UserDetails principal = new User(String.valueOf(member.getId()), "",
                Arrays.stream(new String[]{member.getRole().name()})
                    .map(SimpleGrantedAuthority::new)
                    .toList());
            Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "",
                Arrays.stream(new String[]{member.getRole().name()})
                    .map(SimpleGrantedAuthority::new)
                    .toList());
            TokenResponseDto tokenResponseDto = TokenResponseDto.builder()
                .grantType("Bearer")
                .accessToken(
                    "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTcwMDU4NjkyOH0.lof7WjalCH1gGPy2q7YYi9VTcgn_aoFMwEMQvITtddsUIcJN-YzNODt_RQde5J5dH98NKMXDOvy7YwNlt6BCfg")
                .accessTokenExpiresIn(1700586928520L)
                .refreshToken(
                    "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE3MDExODk5Mjh9.uZuIAxsnf4Ubz5K9YzysJTu9Gh25XNTsPVAPSElw1lS78gS8S08L97Z4RkfGodegGXZ9UFFNkVXdhRzF9Pr-uA")
                .build();
            RefreshToken refreshToken = RefreshToken.builder()
                .id(1L)
                .token(
                    "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE3MDExODk5Mjh9.uZuIAxsnf4Ubz5K9YzysJTu9Gh25XNTsPVAPSElw1lS78gS8S08L97Z4RkfGodegGXZ9UFFNkVXdhRzF9Pr-uA")
                .build();
            given(passwordEncoder.matches(any(),any())).willReturn(true);
            given(memberRepository.findByEmail(any())).willReturn(Optional.of(member));
            given(authenticationManagerBuilder.getObject()).willReturn(authenticationManager);
            given(authenticationManager.authenticate(any(Authentication.class))).willReturn(
                authentication);
            given(jwtTokenProvider.createToken(any(Authentication.class))).willReturn(
                tokenResponseDto);
            given(refreshTokenRepository.save(any(RefreshToken.class))).willReturn(refreshToken);


            // when
            LogInResponseDto result = memberService.loginMember(signInRequestDto);

            // then
            assertNotNull(result);
            assertThat(result.getMember()).extracting("memberId", "email", "nickname", "image")
                .containsExactly(1L, "test@mail.com", "test",
                    "https://fastly.picsum.photos/id/866/200/300.jpg?hmac=rcadCENKh4rD6MAp6V_ma-AyWv641M4iiOpe1RyFHeI");
            assertThat(result.getToken()).extracting("grantType", "accessToken",
                    "accessTokenExpiresIn", "refreshToken")
                .containsExactly("Bearer",
                    "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTcwMDU4NjkyOH0.lof7WjalCH1gGPy2q7YYi9VTcgn_aoFMwEMQvITtddsUIcJN-YzNODt_RQde5J5dH98NKMXDOvy7YwNlt6BCfg",
                    1700586928520L,
                    "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE3MDExODk5Mjh9.uZuIAxsnf4Ubz5K9YzysJTu9Gh25XNTsPVAPSElw1lS78gS8S08L97Z4RkfGodegGXZ9UFFNkVXdhRzF9Pr-uA");

            verify(authenticationManagerBuilder, times(1)).getObject();
            verify(authenticationManager, times(1)).authenticate(any(Authentication.class));
            verify(jwtTokenProvider, times(1)).createToken(any(Authentication.class));
            verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
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

    @Nested
    @DisplayName("refresh()은")
    class Context_refresh {

        @Test
        @DisplayName("토큰을 재발급 할 수 있다.")
        void _willSuccess() {
            // given
            RefreshRequestDto refreshRequestDto = RefreshRequestDto.builder()
                .accessToken(
                    "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTcwMDU4NjkyOH0.lof7WjalCH1gGPy2q7YYi9VTcgn_aoFMwEMQvITtddsUIcJN-YzNODt_RQde5J5dH98NKMXDOvy7YwNlt6BCfg")
                .refreshToken(
                    "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE3MDExODk5Mjh9.uZuIAxsnf4Ubz5K9YzysJTu9Gh25XNTsPVAPSElw1lS78gS8S08L97Z4RkfGodegGXZ9UFFNkVXdhRzF9Pr-uA")
                .build();
            Member member = Member.builder()
                .id(1L)
                .email("test@mail.com")
                .nickname("test")
                .password("$10$ygrAExVYmFTkZn2d0.Pk3Ot5CNZwIBjZH5f.WW0AnUq4w4PtBi9Nm")
                .image(
                    "https://fastly.picsum.photos/id/866/200/300.jpg?hmac=rcadCENKh4rD6MAp6V_ma-AyWv641M4iiOpe1RyFHeI")
                .role(Role.ROLE_USER)
                .build();
            UserDetails principal = new User(String.valueOf(member.getId()), "",
                Arrays.stream(new String[]{member.getRole().name()})
                    .map(SimpleGrantedAuthority::new)
                    .toList());
            Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "",
                Arrays.stream(new String[]{member.getRole().name()})
                    .map(SimpleGrantedAuthority::new)
                    .toList());
            TokenResponseDto tokenResponseDto = TokenResponseDto.builder()
                .grantType("Bearer")
                .accessToken(
                    "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTcwMDU4NjkyOH0.lof7WjalCH1gGPy2q7YYi9VTcgn_aoFMwEMQvITtddsUIcJN-YzNODt_RQde5J5dH98NKMXDOvy7YwNlt6BCfg")
                .accessTokenExpiresIn(1700586928520L)
                .refreshToken(
                    "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE3MDExODk5Mjh9.uZuIAxsnf4Ubz5K9YzysJTu9Gh25XNTsPVAPSElw1lS78gS8S08L97Z4RkfGodegGXZ9UFFNkVXdhRzF9Pr-uA")
                .build();
            RefreshToken refreshToken = RefreshToken.builder()
                .id(1L)
                .token(
                    "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE3MDExODk5Mjh9.uZuIAxsnf4Ubz5K9YzysJTu9Gh25XNTsPVAPSElw1lS78gS8S08L97Z4RkfGodegGXZ9UFFNkVXdhRzF9Pr-uA")
                .build();

            given(jwtTokenProvider.validateToken(any(String.class))).willReturn(true);
            given(jwtTokenProvider.getAuthentication(any(String.class))).willReturn(authentication);
            given(refreshTokenRepository.findById(any(Long.class))).willReturn(
                Optional.of(refreshToken));
            given(jwtTokenProvider.createToken(any(Authentication.class))).willReturn(
                tokenResponseDto);
            given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));


            // when
            LogInResponseDto result = memberService.refresh(refreshRequestDto);

            // then
            assertNotNull(result);
            assertThat(result.getMember()).extracting("memberId", "email", "nickname", "image")
                .containsExactly(1L, "test@mail.com", "test",
                    "https://fastly.picsum.photos/id/866/200/300.jpg?hmac=rcadCENKh4rD6MAp6V_ma-AyWv641M4iiOpe1RyFHeI");
            assertThat(result.getToken()).extracting("grantType", "accessToken",
                    "accessTokenExpiresIn", "refreshToken")
                .containsExactly("Bearer",
                    "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTcwMDU4NjkyOH0.lof7WjalCH1gGPy2q7YYi9VTcgn_aoFMwEMQvITtddsUIcJN-YzNODt_RQde5J5dH98NKMXDOvy7YwNlt6BCfg",
                    1700586928520L,
                    "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE3MDExODk5Mjh9.uZuIAxsnf4Ubz5K9YzysJTu9Gh25XNTsPVAPSElw1lS78gS8S08L97Z4RkfGodegGXZ9UFFNkVXdhRzF9Pr-uA");

            verify(jwtTokenProvider, times(1)).validateToken(any(String.class));
            verify(jwtTokenProvider, times(1)).getAuthentication(any(String.class));
            verify(refreshTokenRepository, times(1)).findById(any(Long.class));
            verify(jwtTokenProvider, times(1)).createToken(any(Authentication.class));
        }

        @Test
        @DisplayName("유효하지 않은 refresh 토큰이라면, 토큰을 재발급 할 수 없다.")
        void invalidToken_willFail() {
            // given
            RefreshRequestDto refreshRequestDto = RefreshRequestDto.builder()
                .accessToken(
                    "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTcwMDU4NjkyOH0.lof7WjalCH1gGPy2q7YYi9VTcgn_aoFMwEMQvITtddsUIcJN-YzNODt_RQde5J5dH98NKMXDOvy7YwNlt6BCfg")
                .refreshToken(
                    "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE3MDExODk5Mjh9.uZuIAxsnf4Ubz5K9YzysJTu9Gh25XNTsPVAPSElw1lS78gS8S08L97Z4RkfGodegGXZ9UFFNkVXdhRzF9Pr-uA")
                .build();

            given(jwtTokenProvider.validateToken(any(String.class))).willReturn(false);

            // when
            Throwable exception = assertThrows(InvalidRefreshTokenException.class, () -> {
                memberService.refresh(refreshRequestDto);
            });

            // then
            assertEquals("Refresh Token 이 유효하지 않습니다.", exception.getMessage());

            verify(jwtTokenProvider, times(1)).validateToken(any(String.class));
        }

        @Test
        @DisplayName("DB에 있는 Refresh 토큰과 일치하지 않는다면, 토큰을 재발급 할 수 없다.")
        void unmatchedMember_willFail() {
            // given
            RefreshRequestDto refreshRequestDto = RefreshRequestDto.builder()
                .accessToken(
                    "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTcwMDU4NjkyOH0.lof7WjalCH1gGPy2q7YYi9VTcgn_aoFMwEMQvITtddsUIcJN-YzNODt_RQde5J5dH98NKMXDOvy7YwNlt6BCfg")
                .refreshToken(
                    "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE3MDExODk5Mjh9.uZuIAxsnf4Ubz5K9YzysJTu9Gh25XNTsPVAPSElw1lS78gS8S08L97Z4RkfGodegGXZ9UFFNkVXdhRzF9Pr-uA")
                .build();
            Member member = Member.builder()
                .id(1L)
                .email("test@mail.com")
                .nickname("test")
                .password("$10$ygrAExVYmFTkZn2d0.Pk3Ot5CNZwIBjZH5f.WW0AnUq4w4PtBi9Nm")
                .image(
                    "https://fastly.picsum.photos/id/866/200/300.jpg?hmac=rcadCENKh4rD6MAp6V_ma-AyWv641M4iiOpe1RyFHeI")
                .role(Role.ROLE_USER)
                .build();
            UserDetails principal = new User(String.valueOf(member.getId()), "",
                Arrays.stream(new String[]{member.getRole().name()})
                    .map(SimpleGrantedAuthority::new)
                    .toList());
            Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "",
                Arrays.stream(new String[]{member.getRole().name()})
                    .map(SimpleGrantedAuthority::new)
                    .toList());
            RefreshToken refreshToken = RefreshToken.builder()
                .id(1L)
                .token(
                    "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE3MDExODk5Mjh9.uZuIAxsnf4Ubz5K9YzysJTu9Gh25XNTsPVAPSElw1lS78gS8S08L97Z4RkfGodegGXZ9UFFNkVXdhRzF9Pr-u")
                .build();

            given(jwtTokenProvider.validateToken(any(String.class))).willReturn(true);
            given(jwtTokenProvider.getAuthentication(any(String.class))).willReturn(authentication);
            given(refreshTokenRepository.findById(any(Long.class))).willReturn(
                Optional.of(refreshToken));

            // when
            Throwable exception = assertThrows(UnmatchedMemberException.class, () -> {
                memberService.refresh(refreshRequestDto);
            });

            // then
            assertEquals("토큰의 회원 정보가 일치하지 않습니다.", exception.getMessage());

            verify(jwtTokenProvider, times(1)).validateToken(any(String.class));
            verify(jwtTokenProvider, times(1)).getAuthentication(any(String.class));
            verify(refreshTokenRepository, times(1)).findById(any(Long.class));
        }
    }

}
