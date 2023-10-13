package com.fasttime.domain.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasttime.domain.member.dto.MemberDto;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.member.request.EditRequest;
import com.fasttime.domain.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private MemberRepository memberRepository;

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
                    .andExpect(
                        MockMvcResultMatchers.content().string("FastCampus에 등록된 이메일이 아닙니다."));
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
                    .andExpect(MockMvcResultMatchers.content().string("이미 사용 중인 닉네임 입니다."));

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
                        MockMvcResultMatchers.content().string("이미 가입된 회원입니다."));

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
                        MockMvcResultMatchers.content().string("회원가입 실패 Something went wrong"));

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
                    .andExpect(MockMvcResultMatchers.content().string("가입 성공!"));

            }

        }

        @Test
        @DisplayName("회원 탈퇴한다.")
        public void testDeleteMember() throws Exception {
            Member member = new Member();
            member.setId(1L);

            MockHttpSession session = new MockHttpSession();
            session.setAttribute("member", member);

            Mockito.doNothing().when(memberService).softDeleteMember(Mockito.any(Member.class));

            mockMvc.perform(MockMvcRequestBuilders.delete("/v1/delete").session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("탈퇴가 완료되었습니다."));

            // 삭제 시간이 갱신되었는지 확인
            Mockito.verify(memberService).softDeleteMember(Mockito.any(Member.class));
        }

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
            member.setNickname("newNickname"); // 수정된 닉네임으로 설정
            member.setImage("newImage"); // 수정된 이미지로 설정

            MockHttpSession session = new MockHttpSession();
            session.setAttribute("member", member); // 수정된 Member 객체로 세션 설정

            EditRequest editRequest = new EditRequest();
            editRequest.setNickname("newNickname");
            editRequest.setImage("newImage");

            Mockito.when(memberService.checkDuplicateNickname("newNickname")).thenReturn(false);

            mockMvc.perform(MockMvcRequestBuilders.put("/v1/retouch-member")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"nickname\": \"newNickname\", \"image\": \"newImage\"}")
                    .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.nickname").value("newNickname")) // 수정된 닉네임
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.email").value("test@example.com")) // 이메일은 그대로
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").isEmpty()); // 메시지 없어야 함
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
            session.setAttribute("member", member);

            EditRequest editRequest = new EditRequest();
            editRequest.setNickname("newNickname");
            editRequest.setImage("newImage");

            Mockito.when(memberService.checkDuplicateNickname("newNickname")).thenReturn(true);

            mockMvc.perform(MockMvcRequestBuilders.put("/v1/retouch-member")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"nickname\": \"newNickname\", \"image\": \"newImage\"}")
                    .session(session))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.message").value("중복된 닉네임입니다."));
        }
    }

    @Test
    @DisplayName("사용자 정보 조회 성공")
    public void testGetMyPageInfo() throws Exception {
        // 가상의 회원 정보를 생성합니다.
        Member member = new Member();
        member.setId(1L);
        member.setEmail("test@example.com");
        member.setNickname("testuser");
        member.setImage("testImage");

        // 가상의 세션을 생성하고 세션에 회원 정보를 설정합니다.
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("MEMBER", member);

        // MemberService의 getMyPageInfo 메서드가 호출될 때 반환할 가상의 MemberDto를 생성합니다.
        MemberDto memberDto = new MemberDto();
        memberDto.setNickname("testuser");
        memberDto.setEmail("test@example.com");
        memberDto.setImage("testImage");

        // MemberService의 getMyPageInfo 메서드가 호출될 때 mock 데이터를 반환하도록 설정합니다.
        Mockito.when(memberService.getMyPageInfo(member)).thenReturn(memberDto);

        // GET /api/v1/mypage 요청을 수행
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/mypage").session(session))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("사용자 정보를 성공적으로 조회하였습니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.nickname").value("testuser"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value("test@example.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.image").value("testImage"));

        // MemberService의 getMyPageInfo 메서드가 session.getAttribute("MEMBER")를 인자로 호출되었는지 확인합니다.
        Mockito.verify(memberService).getMyPageInfo(member);
    }

}

