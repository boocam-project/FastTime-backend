package com.fasttime.domain.member.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasttime.domain.member.dto.request.LoginRequestDTO;
import com.fasttime.domain.member.dto.MemberDto;
import com.fasttime.domain.member.request.RePasswordRequest;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.global.interceptor.LoginCheckInterceptor;
import org.apache.juli.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerTestForAuto {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MemberController memberController;


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
        @WithMockUser
        void _willSuccess() throws Exception {
            //given
            LoginRequestDTO dto = new LoginRequestDTO("testEmail", "testPassword");
            //when,then
            String s = om.writeValueAsString(dto);
            mockMvc.perform(post("/v1/login")
                    .content(s)
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").exists())
                .andExpect(jsonPath("$.data.nickname").exists())
                .andDo(print());

        }

        @DisplayName("검증으로 인해 실패한다.")
        @Test
        @WithMockUser
        void Validation_willFail() throws Exception {
            //given
            LoginRequestDTO dto = new LoginRequestDTO(" ", "testPassword");
            //when,then
            String s = om.writeValueAsString(dto);
            mockMvc.perform(post("/v1/login")
                    .with(csrf())
                    .content(s)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("must not be blank"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(print());
        }
        @DisplayName("등록되지않는 이메일로 인해 실패한다.")
        @Test
        @WithMockUser
        void Email_willFail() throws Exception {
            //given
            LoginRequestDTO dto = new LoginRequestDTO("email", "testPassword");
            //when,then
            String s = om.writeValueAsString(dto);
            mockMvc.perform(post("/v1/login")
                    .content(s)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message")
                    .value("User not found with email: email"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(print());
        }
        @DisplayName("비밀번호가 달라 실패한다.")
        @Test
        @WithMockUser
        void password_willFail() throws Exception {
            //given
            LoginRequestDTO dto = new LoginRequestDTO("testEmail", "Password");
            //when,then
            String s = om.writeValueAsString(dto);
            mockMvc.perform(post("/v1/login")
                    .content(s)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.data.error")
                    .value("Not match password!"))
                .andDo(print());
        }

    }

    @DisplayName("로그아웃을")
    @Nested
    class LogOut {

        @DisplayName("성공한다.")
        @Test
        void _willSuccess() throws Exception {
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("MEMBER", "test@naver.com");
            mockMvc.perform(get("/v1/logout")
                    .session(session))
                .andExpect(status().isOk());
        }

        @DisplayName("인터셉터로 인해 실패한다.")
        @Test
        void Interceptor_willFail() throws Exception {
            //given
            mockMvc = MockMvcBuilders.standaloneSetup(memberController)
                .addInterceptors(new LoginCheckInterceptor()).build();

            //when, then
            mockMvc.perform(get("/v1/logout"))
                .andExpect(redirectedUrl("/v1/login"));
        }
    }
    @DisplayName("비밀번호 재설정을")
    @Nested
    class RePassword {
        @BeforeEach
        void testMember() {
            MemberDto memberDto = new MemberDto("testEmail",
                "testPassword", "testNickname");
            memberService.save(memberDto);
        }
        @DisplayName("성공한다.")
        @Test
        void _willSuccess() throws Exception {
            //given
            RePasswordRequest request = new RePasswordRequest
                ("testEmail", "newPassword", "newPassword");
            //when, the
            String s = om.writeValueAsString(request);
            mockMvc.perform(post("/v1/RePassword")
                    .content(s)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").exists())
                .andExpect(jsonPath("$.data.nickname").exists())
                .andDo(print());
        }
        @DisplayName("검증으로 인해 실패한다.")
        @Test
        void validation_willFail() throws Exception {
            //given
            RePasswordRequest request = new RePasswordRequest
                ("testEmail", " ", "newPassword");
            //when, the
            String s = om.writeValueAsString(request);
            mockMvc.perform(post("/v1/RePassword")
                    .content(s)
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
                ("testEmail", "newPassword", "new");
            //when, the
            String s = om.writeValueAsString(request);
            mockMvc.perform(post("/v1/RePassword")
                    .content(s)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.data.error").value("Not Match RePassword!"))
                .andDo(print());

        }
    }
}
