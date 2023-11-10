package com.fasttime.domain.member.unit.controller;


import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasttime.domain.member.controller.MemberController;
import com.fasttime.domain.member.dto.MemberDto;
import com.fasttime.domain.member.dto.request.LoginRequestDTO;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.exception.UserNotMatchInfoException;
import com.fasttime.domain.member.exception.UserNotMatchRePasswordException;
import com.fasttime.domain.member.exception.UserSoftDeletedException;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.member.request.EditRequest;
import com.fasttime.domain.member.request.RePasswordRequest;
import com.fasttime.domain.member.response.MemberResponse;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.global.interceptor.LoginCheckInterceptor;
import com.fasttime.global.util.ResponseDTO;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest(MemberController.class)
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private MemberRepository memberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("회원가입은")
    class Join {

        @Nested
        @DisplayName("실패한다.")
        class Join_fail {

            @Test
            @DisplayName("이미 가입된 회원일 때")
            public void alreadyRegisteredMember() throws Exception {
                MemberDto memberDto = new MemberDto();
                memberDto.setEmail("test@example.com");
                memberDto.setPassword("password");
                memberDto.setNickname("testuser");

                when(memberService.registerOrRecoverMember(any(MemberDto.class)))
                    .thenReturn(ResponseDTO.res(HttpStatus.BAD_REQUEST, "이미 가입된 회원입니다."));

                ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/join")
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
            public void duplicateNickname() throws Exception {
                MemberDto memberDto = new MemberDto();
                memberDto.setEmail("test@example.com");
                memberDto.setPassword("password");
                memberDto.setNickname("testuser");

                when(memberService.registerOrRecoverMember(any(MemberDto.class)))
                    .thenReturn(ResponseDTO.res(HttpStatus.BAD_REQUEST, "이미 사용 중인 닉네임 입니다."));

                ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/join")
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
            public void join_Success() throws Exception {
                MemberDto memberDto = new MemberDto();
                memberDto.setEmail("test@example.com");
                memberDto.setPassword("password");
                memberDto.setNickname("testuser");

                when(memberService.registerOrRecoverMember(any(MemberDto.class)))
                    .thenReturn(ResponseDTO.res(HttpStatus.OK, "가입 성공!"));

                ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/join")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
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
        public void testDeleteMember() throws Exception {
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
            @DisplayName("성공한다. : 프로필 수정")
            public void UpdateMember_Success() throws Exception {
                Member member = new Member();
                member.setId(1L);
                member.setEmail("test@example.com");
                member.setPassword("password");
                member.setNickname("oldNickname");
                member.setImage("oldImage");

                EditRequest editRequest = new EditRequest();
                editRequest.setNickname("newNickname");
                editRequest.setImage("newImage");

                MockHttpSession session = new MockHttpSession();
                session.setAttribute("MEMBER", member.getId());

                given(memberService.getMember(member.getId())).willReturn(member);
                given(memberService.checkDuplicateNickname("newNickname")).willReturn(false);

                given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

                mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/retouch-member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nickname\": \"newNickname\", \"image\": \"newImage\"}")
                        .session(session))
                    .andExpect(status().isOk())
                    .andExpect(
                        MockMvcResultMatchers.jsonPath("$.data.nickname").value("newNickname"));
            }

            @Test
            @DisplayName("실패한다. : 중복된 닉네임으로 변경")
            public void UpdateMemberWithDuplicateNickname() throws Exception {
                Member member = new Member();
                member.setId(1L);
                member.setEmail("test@example.com");
                member.setPassword("password");
                member.setNickname("oldNickname");
                member.setImage("oldImage");

                MockHttpSession session = new MockHttpSession();
                session.setAttribute("MEMBER", member.getId());

                given(memberService.checkDuplicateNickname("newNickname")).willReturn(true);

                given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

                mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/retouch-member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nickname\": \"newNickname\", \"image\": \"newImage\"}")
                        .session(session))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.data.message").value("중복된 닉네임입니다."));
            }

        }


        @Nested
        @DisplayName("마이페이지 조회")
        class MyPage {

            @Test
            @DisplayName("성공한다. : 로그인된 사용자 정보 조회")
            public void testGetMyPageInfoWhenLoggedIn() throws Exception {
                Member loggedInMember = new Member();
                loggedInMember.setId(1L);
                loggedInMember.setNickname("testuser");
                loggedInMember.setEmail("test@example.com");
                loggedInMember.setImage("testImage");

                MockHttpSession session = new MockHttpSession();
                session.setAttribute("MEMBER", loggedInMember.getId());

                given(memberService.getMember(loggedInMember.getId())).willReturn(loggedInMember);

                mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/mypage").session(session))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data.nickname").value("testuser"))
                    .andExpect(
                        MockMvcResultMatchers.jsonPath("$.data.email").value("test@example.com"))
                    .andExpect(
                        MockMvcResultMatchers.jsonPath("$.data.profileImageUrl")
                            .value("testImage"));
            }

            @Test
            @DisplayName("실패한다. : 로그인하지 않은 사용자 정보 조회")
            public void testGetMyPageInfoWhenNotLoggedIn() throws Exception {
                MockHttpSession session = new MockHttpSession();

                mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/mypage").session(session))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message")
                        .value("사용자가 로그인되어 있지 않습니다."));
            }
        }
    }

    @DisplayName("loginMember()는")
    @Nested
    class Login {

        @DisplayName("로그인을 성공한다.")
        @Test
        void _willSuccess() throws Exception {
            //given
            LoginRequestDTO dto = new LoginRequestDTO("testEmail", "testPassword");
            MemberResponse memberResponse = new MemberResponse(1L, "땅땅띠라랑");
            when(memberService.loginMember(any(LoginRequestDTO.class))).thenReturn(memberResponse);
            String data = objectMapper.writeValueAsString(dto);

            //when,then
            mockMvc.perform(post("/api/v1/login")
                    .content(data)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.nickname").isString())
                .andDo(print());

        }

        @DisplayName("로그인을 검증으로 인해 실패한다.")
        @Test
        void Validation_willFail() throws Exception {
            //given
            LoginRequestDTO dto = new LoginRequestDTO("", "testPassword");
            String data = objectMapper.writeValueAsString(dto);
            //when,then
            mockMvc.perform(post("/api/v1/login")
                    .content(data)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("must not be blank"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(print());
        }

        @DisplayName("로그인을 등록되지않는 이메일로 인해 실패한다.")
        @Test
        void Email_willFail() throws Exception {
            //given
            LoginRequestDTO dto = new LoginRequestDTO("email", "testPassword");
            String data = objectMapper.writeValueAsString(dto);
            when(memberService.loginMember(any(LoginRequestDTO.class)))
                .thenThrow(new UserNotMatchInfoException());
            //when,then
            mockMvc.perform(post("/api/v1/login")
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
        void password_willFail() throws Exception {
            //given
            LoginRequestDTO dto = new LoginRequestDTO("testEmail", "Password");
            String data = objectMapper.writeValueAsString(dto);
            when(memberService.loginMember(any(LoginRequestDTO.class)))
                .thenThrow(new UserNotMatchInfoException());
            //when,then
            mockMvc.perform(post("/api/v1/login")
                    .content(data)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message")
                    .value("아이디 또는 비밀번호가 일치하지 않습니다."))
                .andDo(print());
        }

        @DisplayName("로그인을 이미 탈퇴한 회원이라서 실패한다.")
        @Test
        void softDeleted_willFail() throws Exception {
            //given
            LoginRequestDTO dto = new LoginRequestDTO("testEmail", "Password");
            String data = objectMapper.writeValueAsString(dto);
            when(memberService.loginMember(any(LoginRequestDTO.class)))
                .thenThrow(new UserSoftDeletedException());
            //when,then
            mockMvc.perform(post("/api/v1/login")
                    .content(data)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message")
                    .value("이미 탈퇴한 회원입니다."))
                .andDo(print());
        }

    }

    @DisplayName("logout()은 ")
    @Nested
    class LogOut {

        @DisplayName("로그아웃을 성공한다.")
        @Test
        void _willSuccess() throws Exception {
            //given
            MockHttpSession session = new MockHttpSession();

            //when, then
            session.setAttribute("MEMBER", "test@naver.com");
            mockMvc.perform(get("/api/v1/logout")
                    .session(session))
                .andExpect(status().isOk());
        }

        @DisplayName("인터셉터로 인해 실패한다.")
        @Test
        void Interceptor_willFail() throws Exception {
            //given
            mockMvc = MockMvcBuilders.standaloneSetup
                    (new MemberController(memberService, memberRepository))
                .addInterceptors(new LoginCheckInterceptor()).build();

            //when, then
            mockMvc.perform(get("/api/v1/logout"))
                .andExpect(status().isForbidden());
        }
    }

    @DisplayName("rePassword()는")
    @Nested
    class RePassword {

        @DisplayName("비밀번호 재설정을 성공한다.")
        @Test
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
        void Re_willFail() throws Exception {
            //given
            RePasswordRequest request = new RePasswordRequest
                ("newPassword", "new");
            when(memberService.rePassword(any(RePasswordRequest.class), anyLong()))
                .thenThrow(new UserNotMatchRePasswordException());
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

}






