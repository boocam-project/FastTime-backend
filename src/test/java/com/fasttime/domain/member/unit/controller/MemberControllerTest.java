package com.fasttime.domain.member.unit.controller;


import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasttime.domain.member.dto.request.CreateMemberDTO;
import com.fasttime.domain.member.dto.request.EditRequest;
import com.fasttime.domain.member.dto.request.LoginRequestDTO;
import com.fasttime.domain.member.dto.request.RePasswordRequest;
import com.fasttime.domain.member.dto.request.RefreshRequestDto;
import com.fasttime.domain.member.dto.response.LogInResponseDto;
import com.fasttime.domain.member.dto.response.MemberResponse;
import com.fasttime.domain.member.dto.response.MemberResponseDto;
import com.fasttime.domain.member.dto.response.MyPageInfoDTO;
import com.fasttime.domain.member.dto.response.TokenResponseDto;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.exception.MemberNotMatchInfoException;
import com.fasttime.domain.member.exception.MemberNotMatchRePasswordException;
import com.fasttime.domain.member.exception.MemberSoftDeletedException;
import com.fasttime.global.exception.ErrorCode;
import com.fasttime.global.util.ResponseDTO;
import com.fasttime.util.ControllerUnitTestSupporter;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


class MemberControllerTest extends ControllerUnitTestSupporter{


    @Nested
    @DisplayName("회원가입은")
    class Join {

        @Nested
        @DisplayName("실패한다.")
        class Join_fail {

            @Test
            @DisplayName("이미 가입된 회원일 때")
            void alreadyRegisteredMember() throws Exception {
                when(
                    memberService.registerOrRecoverMember(any(CreateMemberDTO.class)))
                    .thenReturn(ResponseDTO.res(HttpStatus.BAD_REQUEST, "이미 가입된 회원입니다."));

                ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            "{\"email\": \"test@example.com\", \"password\": \"password\", \"nickname\": \"testuser\"}")
                );

                String response = new String(
                    resultActions.andReturn().getResponse().getContentAsByteArray(),
                    StandardCharsets.UTF_8);

                assertTrue(response.contains("이미 가입된 회원입니다."));

                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            @DisplayName("닉네임이 중복일 때")
            void duplicateNickname() throws Exception {

                when(memberService.registerOrRecoverMember(any(CreateMemberDTO.class)))
                    .thenReturn(ResponseDTO.res(HttpStatus.BAD_REQUEST, "이미 사용 중인 닉네임 입니다."));

                ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            "{\"email\": \"test@example.com\", \"password\": \"password\", \"nickname\": \"testuser\"}")
                );

                String response = new String(
                    resultActions.andReturn().getResponse().getContentAsByteArray(),
                    StandardCharsets.UTF_8);

                assertTrue(response.contains("이미 사용 중인 닉네임 입니다."));

                resultActions.andExpect(status().isBadRequest());
            }
        }

        @Nested
        @DisplayName("성공한다.")
        class Join_Success {

            @Test
            @DisplayName("회원가입 성공")
            void join_Success() throws Exception {
                when(memberService.registerOrRecoverMember(any(CreateMemberDTO.class)))
                    .thenReturn(ResponseDTO.res(HttpStatus.OK, "가입 성공!"));

                ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            "{\"email\": \"test@example.com\", \"password\": \"password\", \"nickname\": \"testuser\"}")
                );

                String response = new String(
                    resultActions.andReturn().getResponse().getContentAsByteArray(),
                    StandardCharsets.UTF_8);

                assertTrue(response.contains("가입 성공!"));

                resultActions.andExpect(status().isOk());
            }
        }


        @Test
        @DisplayName("회원 탈퇴한다.")
        void testDeleteMember() throws Exception {
            Member member = new Member();
            member.setId(1L);

            MockHttpSession session = new MockHttpSession();
            session.setAttribute("MEMBER", member.getId());

            given(memberService.getMember(member.getId())).willReturn(member);

            doNothing().when(memberService).softDeleteMember(member);

            mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/delete").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("탈퇴가 완료되었습니다."));

            verify(memberService).softDeleteMember(member);
        }


        @Nested
        @DisplayName("회원 정보 수정")
        class UpdateMember {

            @Test
            @DisplayName("성공한다: 프로필 수정")
            void updateMember_Success() throws Exception {
                // Given
                Member member = new Member();
                member.setId(1L);
                member.setEmail("test@example.com");
                member.setPassword("password");
                member.setNickname("oldNickname");
                member.setImage("oldImage");

                EditRequest editRequest = new EditRequest();
                editRequest.setNickname("newNickname");
                editRequest.setImage("newImage");

                Member updatedMember = new Member();
                updatedMember.setId(member.getId());
                updatedMember.setEmail(member.getEmail());
                updatedMember.setPassword(member.getPassword());
                updatedMember.setNickname(editRequest.getNickname());
                updatedMember.setImage(editRequest.getImage());

                MockHttpSession session = new MockHttpSession();
                session.setAttribute("MEMBER", member.getId());

                given(
                    memberService.updateMemberInfo(any(EditRequest.class), anyLong()))
                    .willReturn(Optional.of(updatedMember));

                // When & Then
                mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/retouch-member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editRequest))
                        .session(session))
                    .andExpect(status().isOk())
                    .andExpect(
                        MockMvcResultMatchers.jsonPath("$.data.nickname").value("newNickname"))
                    .andDo(print());
            }


            @Test
            @DisplayName("실패한다: 사용자를 찾을 수 없음")
            void updateMember_NotFound() throws Exception {
                // Given
                EditRequest editRequest = new EditRequest();
                editRequest.setNickname("newNickname");
                editRequest.setImage("newImage");

                MockHttpSession session = new MockHttpSession();
                session.setAttribute("MEMBER", 1L);

                given(
                    memberService.updateMemberInfo(any(EditRequest.class), anyLong()))
                    .willReturn(Optional.empty());

                // When & Then
                mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/retouch-member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editRequest))
                        .session(session))
                    .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.message").value(ErrorCode.MEMBER_NOT_FOUND.getMessage()))
                    .andDo(print());
            }
        }
    }

    @Nested
    @DisplayName("마이페이지 조회")
    class MyPage {

        @Test
        @DisplayName("성공한다. : 로그인된 사용자 정보 조회")
        void testGetMyPageInfoWhenLoggedIn() throws Exception {
            // Given
            Member loggedInMember = new Member();
            loggedInMember.setId(1L);
            loggedInMember.setNickname("testuser");
            loggedInMember.setEmail("test@example.com");
            loggedInMember.setImage("testImage");

            MockHttpSession session = new MockHttpSession();
            session.setAttribute("MEMBER", loggedInMember.getId());

            MyPageInfoDTO myPageInfoDto = new MyPageInfoDTO(
                loggedInMember.getNickname(),
                loggedInMember.getImage(),
                loggedInMember.getEmail()
            );

            given(memberService.getMyPageInfoById(any(Long.class))).willReturn(myPageInfoDto);

            // Then
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/mypage").session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.nickname").value("testuser"))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.email").value("test@example.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.image").value("testImage"));

        }

        @Test
        @DisplayName("실패한다. : 로그인하지 않은 사용자 정보 조회")
        void testGetMyPageInfoWhenNotLoggedIn() throws Exception {
            // Given
            MockHttpSession session = new MockHttpSession();

            // Then
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/mypage").session(session))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("접근 권한이 없습니다."));
        }
    }

    @DisplayName("loginMember()는")
    @Nested
    class Login {
        @Test
        @DisplayName("로그인 할 수 있다.")
        void _willSuccess() throws Exception {
            // given
            LoginRequestDTO request = LoginRequestDTO.builder()
                .email("test@mail.com")
                .password("qwer1234$$")
                .build();
            MemberResponseDto memberResponseDto = MemberResponseDto.builder()
                .memberId(1L)
                .email("test@mail.com")
                .nickname("test")
                .image("")
                .build();
            TokenResponseDto tokenResponseDto = TokenResponseDto.builder()
                .grantType("Bearer")
                .accessToken(
                    "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTcwMDU4NjkyOH0.lof7WjalCH1gGPy2q7YYi9VTcgn_aoFMwEMQvITtddsUIcJN-YzNODt_RQde5J5dH98NKMXDOvy7YwNlt6BCfg")
                .accessTokenExpiresIn(1700586928520L)
                .refreshToken(
                    "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE3MDExODk5Mjh9.uZuIAxsnf4Ubz5K9YzysJTu9Gh25XNTsPVAPSElw1lS78gS8S08L97Z4RkfGodegGXZ9UFFNkVXdhRzF9Pr-uA")
                .build();
            LogInResponseDto logInResponseDto = LogInResponseDto.builder()
                .member(memberResponseDto)
                .token(tokenResponseDto)
                .build();

            given(memberService.loginMember(any(LoginRequestDTO.class))).willReturn(logInResponseDto);

            // when then
            mockMvc.perform(post("/api/v2/login")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").isNumber())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.member").isMap())
                .andExpect(jsonPath("$.data.member.memberId").isNumber())
                .andExpect(jsonPath("$.data.member.email").isString())
                .andExpect(jsonPath("$.data.member.nickname").isString())
                .andExpect(jsonPath("$.data.member.image").isString())
                .andExpect(jsonPath("$.data.token").isMap())
                .andExpect(jsonPath("$.data.token.grantType").isString())
                .andExpect(jsonPath("$.data.token.accessToken").isString())
                .andExpect(jsonPath("$.data.token.accessTokenExpiresIn").isNumber())
                .andExpect(jsonPath("$.data.token.refreshToken").isString())
                .andDo(print());
        }


        @DisplayName("로그인을 검증으로 인해 실패한다.")
        @Test
        @WithMockUser
        void Validation_willFail() throws Exception {
            //given
            LoginRequestDTO dto = new LoginRequestDTO("", "testPassword");
            String data = objectMapper.writeValueAsString(dto);
            //when,then
            mockMvc.perform(post("/api/v2/login")
                    .content(data)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("must not be blank"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(print());
        }

        @DisplayName("로그인을 등록되지않는 이메일로 인해 실패한다.")
        @Test
        @WithMockUser
        void Email_willFail() throws Exception {
            //given
            LoginRequestDTO dto = new LoginRequestDTO("email@gmail.com", "testPassword");
            String data = objectMapper.writeValueAsString(dto);
            when(memberService.loginMember(any(LoginRequestDTO.class)))
                .thenThrow(new MemberNotMatchInfoException());
            //when,then
            mockMvc.perform(post("/api/v2/login")
                    .content(data)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message")
                    .value("아이디 또는 비밀번호가 일치하지 않습니다."))
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(print());
        }

        @DisplayName("로그인을 비밀번호가 달라 실패한다.")
        @Test
        @WithMockUser
        void password_willFail() throws Exception {
            //given
            LoginRequestDTO dto = new LoginRequestDTO("email@gmail.com", "Password");
            String data = objectMapper.writeValueAsString(dto);
            when(memberService.loginMember(any(LoginRequestDTO.class)))
                .thenThrow(new MemberNotMatchInfoException());
            //when,then
            mockMvc.perform(post("/api/v2/login")
                    .content(data)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message")
                    .value("아이디 또는 비밀번호가 일치하지 않습니다."))
                .andDo(print());
        }

        @DisplayName("로그인을 이미 탈퇴한 회원이라서 실패한다.")
        @Test
        @WithMockUser
        void softDeleted_willFail() throws Exception {
            //given
            LoginRequestDTO dto = new LoginRequestDTO("email@gmail.com", "Password");
            String data = objectMapper.writeValueAsString(dto);
            when(memberService.loginMember(any(LoginRequestDTO.class)))
                .thenThrow(new MemberSoftDeletedException());
            //when,then
            mockMvc.perform(post("/api/v2/login")
                    .content(data)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message")
                    .value("이미 탈퇴한 회원입니다."))
                .andDo(print());
        }

    }


    @DisplayName("rePassword()는")
    @Nested
    class RePassword {

        @DisplayName("비밀번호 재설정을 성공한다.")
        @Test
        @WithMockUser
        void _willSuccess() throws Exception {
            //given
            RePasswordRequest request = new RePasswordRequest
                ("newPassword", "newPassword");
            MemberResponse memberResponse = new MemberResponse(1L, "땅땅띠라랑");
            when(memberService.rePassword(any(RePasswordRequest.class), anyLong()))
                .thenReturn(memberResponse);

            MockHttpSession session = new MockHttpSession();
            session.setAttribute("MEMBER", 1L);

            String data = objectMapper.writeValueAsString(request);

            //when, then
            mockMvc.perform(post("/api/v1/RePassword")
                    .content(data)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.nickname").exists())
                .andDo(print());
        }

        @DisplayName("비밀번호 재설정을 검증으로 인해 실패한다.")
        @Test
        @WithMockUser
        void validation_willFail() throws Exception {
            //given
            RePasswordRequest request = new RePasswordRequest
                ("", "newPassword");

            String data = objectMapper.writeValueAsString(request);

            //when, then
            mockMvc.perform(post("/api/v1/RePassword")
                    .content(data)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("must not be blank"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(print());
        }

        @DisplayName("비밀번호 재확인이 일치하지 않음으로 실패한다.")
        @Test
        @WithMockUser
        void Re_willFail() throws Exception {
            //given
            RePasswordRequest request = new RePasswordRequest
                ("newPassword", "new");
            when(memberService.rePassword(any(RePasswordRequest.class), anyLong()))
                .thenThrow(new MemberNotMatchRePasswordException());
            String data = objectMapper.writeValueAsString(request);

            MockHttpSession session = new MockHttpSession();
            session.setAttribute("MEMBER", 1L);

            //when, then
            mockMvc.perform(post("/api/v1/RePassword")
                    .content(data)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("비밀번호 재확인이 일치하지 않습니다."))
                .andDo(print());
        }
    }
    @Nested
    @DisplayName("refresh()은")
    class Context_refresh {

        @Test
        @DisplayName("토큰을 재발급 할 수 있다.")
        void _willSuccess() throws Exception {
            // given
            RefreshRequestDto request = RefreshRequestDto.builder()
                .accessToken(
                    "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTcwMDU4NjkyOH0.lof7WjalCH1gGPy2q7YYi9VTcgn_aoFMwEMQvITtddsUIcJN-YzNODt_RQde5J5dH98NKMXDOvy7YwNlt6BCfg")
                .refreshToken(
                    "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE3MDExODk5Mjh9.uZuIAxsnf4Ubz5K9YzysJTu9Gh25XNTsPVAPSElw1lS78gS8S08L97Z4RkfGodegGXZ9UFFNkVXdhRzF9Pr-uA")
                .build();
            MemberResponseDto memberResponseDto = MemberResponseDto.builder()
                .memberId(1L)
                .email("test@mail.com")
                .nickname("test")
                .image("")
                .build();
            TokenResponseDto tokenResponseDto = TokenResponseDto.builder()
                .grantType("Bearer")
                .accessToken(
                    "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTcwMDU4NjkyOH0.lof7WjalCH1gGPy2q7YYi9VTcgn_aoFMwEMQvITtddsUIcJN-YzNODt_RQde5J5dH98NKMXDOvy7YwNlt6BCfg")
                .accessTokenExpiresIn(1700586928520L)
                .refreshToken(
                    "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE3MDExODk5Mjh9.uZuIAxsnf4Ubz5K9YzysJTu9Gh25XNTsPVAPSElw1lS78gS8S08L97Z4RkfGodegGXZ9UFFNkVXdhRzF9Pr-uA")
                .build();
            LogInResponseDto logInResponseDto = LogInResponseDto.builder()
                .member(memberResponseDto)
                .token(tokenResponseDto)
                .build();

            given(memberService.refresh(any(RefreshRequestDto.class))).willReturn(logInResponseDto);

            // when then
            mockMvc.perform(post("/api/v2/refresh")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").isNumber())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.member").isMap())
                .andExpect(jsonPath("$.data.member.memberId").isNumber())
                .andExpect(jsonPath("$.data.member.email").isString())
                .andExpect(jsonPath("$.data.member.nickname").isString())
                .andExpect(jsonPath("$.data.member.image").isString())
                .andExpect(jsonPath("$.data.token").isMap())
                .andExpect(jsonPath("$.data.token.grantType").isString())
                .andExpect(jsonPath("$.data.token.accessToken").isString())
                .andExpect(jsonPath("$.data.token.accessTokenExpiresIn").isNumber())
                .andExpect(jsonPath("$.data.token.refreshToken").isString())
                .andDo(print());
        }
    }
}






