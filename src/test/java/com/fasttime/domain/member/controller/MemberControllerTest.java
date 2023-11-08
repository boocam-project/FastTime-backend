package com.fasttime.domain.member.controller;


import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasttime.domain.member.dto.MemberDto;
import com.fasttime.domain.member.dto.request.MyPageInfoDTO;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.member.request.EditRequest;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.global.exception.ErrorCode;
import com.fasttime.global.util.ResponseDTO;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
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
            @DisplayName("성공한다: 프로필 수정")
            public void updateMember_Success() throws Exception {
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
                    memberService.updateMemberInfo(any(EditRequest.class), any(HttpSession.class)))
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
            public void updateMember_NotFound() throws Exception {
                // Given
                EditRequest editRequest = new EditRequest();
                editRequest.setNickname("newNickname");
                editRequest.setImage("newImage");

                MockHttpSession session = new MockHttpSession();
                session.setAttribute("MEMBER", 1L);

                given(
                    memberService.updateMemberInfo(any(EditRequest.class), any(HttpSession.class)))
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
        public void testGetMyPageInfoWhenLoggedIn() throws Exception {
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
        public void testGetMyPageInfoWhenNotLoggedIn() throws Exception {
            // Given
            MockHttpSession session = new MockHttpSession();

            // Then
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/mypage").session(session))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("접근 권한이 없습니다."));

        }
    }

}






