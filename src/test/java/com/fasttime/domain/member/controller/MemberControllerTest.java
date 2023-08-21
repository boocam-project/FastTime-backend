package com.fasttime.domain.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasttime.domain.member.dto.MemberDto;
import com.fasttime.domain.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @InjectMocks
    private MemberController memberController;

    @Nested
    @DisplayName("회원가입은")
    class Join {

        @Nested
        @DisplayName("실패한다.")
        class Join_fail {

            @DisplayName("수강생이 아닐 경우")
            @Test
            public void NotStudentEmail() throws Exception {
                //fc_member에 등록된 수강생 x , 최초회원가입 o
                MemberDto memberDto = new MemberDto();
                memberDto.setEmail("test@example.com");
                memberDto.setPassword("password");
                memberDto.setNickname("testuser");

                doThrow(DuplicateKeyException.class).when(memberService).save(any(MemberDto.class));

                mockMvc.perform(MockMvcRequestBuilders.post("/v1/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"test@example.com\", \"password\": \"password\"}")
                    )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string("가입 실패: 수강생 이메일이 아닙니다."));
                verify(memberService, never()).save(memberDto);
            }


            @Test
            @DisplayName("닉네임 중복일 경우")
            public void AlreadyExistsInNickname() throws Exception {
                //fc_member에 등록된 수강생 o , 최초회원가입 o
                MemberDto memberDto = new MemberDto();
                memberDto.setEmail("test@example.com");
                memberDto.setPassword("password");
                memberDto.setNickname("testuser");

                when(memberService.isEmailExistsInFcmember(memberDto.getEmail())).thenReturn(true);
                when(memberService.isEmailExistsInMember(memberDto.getEmail())).thenReturn(false);
                when(memberService.checkDuplicateNickname(memberDto.getNickname())).thenReturn(
                    true);

                mockMvc.perform(MockMvcRequestBuilders.post("/v1/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            "{\"email\": \"test@example.com\", \"password\": \"password\", \"nickname\": \"testuser\"}")
                    )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string("가입 실패: 중복된 닉네임."));

                verify(memberService, never()).save(memberDto);
            }

            @Test
            @DisplayName("이미 가입된 회원일 경우")
            public void AlreadyExistsInMember() throws Exception {
                //fc_member에 등록된 수강생 o , 최초회원가입 x
                MemberDto memberDto = new MemberDto();
                memberDto.setEmail("test@example.com");
                memberDto.setPassword("password");
                memberDto.setNickname("testuser");

                when(memberService.isEmailExistsInFcmember(memberDto.getEmail())).thenReturn(true);
                when(memberService.isEmailExistsInMember(memberDto.getEmail())).thenReturn(true);
                when(memberService.checkDuplicateNickname(memberDto.getNickname())).thenReturn(
                    false);

                mockMvc.perform(MockMvcRequestBuilders.post("/v1/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            "{\"email\": \"test@example.com\", \"password\": \"password\", \"nickname\": \"testuser\"}")
                    )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(
                        MockMvcResultMatchers.content().string("가입 실패: 해당 이메일은 이미 회원 목록에 존재합니다"));

                verify(memberService, never()).save(memberDto);
            }


            @Test
            @DisplayName("그 외의 경우")
            public void testJoin_Exception() throws Exception {
                MemberDto memberDto = new MemberDto();
                memberDto.setEmail("test@example.com");
                memberDto.setPassword("password");
                memberDto.setNickname("testuser");

                when(memberService.isEmailExistsInFcmember(memberDto.getEmail())).thenThrow(
                    new RuntimeException("Something went wrong"));

                mockMvc.perform(MockMvcRequestBuilders.post("/v1/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            "{\"email\": \"test@example.com\", \"password\": \"password\", \"nickname\": \"testuser\"}")
                    )
                    .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                    .andExpect(
                        MockMvcResultMatchers.content().string("회원가입 실패: Something went wrong"));

                verify(memberService, never()).save(memberDto);
            }
        }

        @Nested
        @DisplayName("성공한다.")
        class Join_Success {

            @Test
            @DisplayName("회원가입 성공")
            public void join_Success() throws Exception {
                //fc_member에 등록된 수강생 이메일 o, 최초 회원가입 o(+닉네임 중복 추가 예정)
                MemberDto memberDto = new MemberDto();
                memberDto.setEmail("test@example.com");
                memberDto.setPassword("password");
                memberDto.setNickname("testuser");

                when(memberService.isEmailExistsInFcmember(memberDto.getEmail())).thenReturn(true);
                when(memberService.isEmailExistsInMember(memberDto.getEmail())).thenReturn(false);
                when(memberService.checkDuplicateNickname(memberDto.getNickname())).thenReturn(
                    false);
                doNothing().when(memberService).save(memberDto);

                mockMvc.perform(MockMvcRequestBuilders.post("/v1/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            "{\"email\": \"test@example.com\", \"password\": \"password\", \"nickname\": \"testuser\"}")
                    )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().string("가입 성공"));

            }
        }

    }
}
