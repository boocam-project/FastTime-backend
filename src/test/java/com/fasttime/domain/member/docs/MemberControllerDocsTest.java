package com.fasttime.domain.member.docs;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.snippet.Attributes.key;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasttime.docs.RestDocsSupport;
import com.fasttime.domain.member.controller.MemberController;
import com.fasttime.domain.member.dto.MemberDto;
import com.fasttime.domain.member.dto.request.LoginRequestDTO;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.member.request.EditRequest;
import com.fasttime.domain.member.request.RePasswordRequest;
import com.fasttime.domain.member.response.MemberResponse;
import com.fasttime.domain.member.service.MemberService;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class MemberControllerDocsTest extends RestDocsSupport {

    private final MemberService memberService = mock(MemberService.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);

    @Override
    public Object initController() {
        return new MemberController(memberService, memberRepository);
    }

    @DisplayName("회원 로그인 API 문서화")
    @Test
    void login() throws Exception {
        //given
        LoginRequestDTO dto = new LoginRequestDTO("123@gmail.com", "testPassword");
        MemberResponse memberResponse = new MemberResponse(1L, "땅땅띠라랑");
        when(memberService.loginMember(any(LoginRequestDTO.class))).thenReturn(memberResponse);
        String data = new ObjectMapper().writeValueAsString(dto);

        //when then
        mockMvc.perform(post("/api/v1/login").contentType(MediaType.APPLICATION_JSON)
                .content(data))
            .andExpect(status().isOk())
            .andDo(document("member-login",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("email").type(JsonFieldType.STRING).description("이메일")
                        .attributes(key("constraints").value("Not Blank")),
                    fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                        .attributes(key("constraints").value("Not Blank"))),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).optional()
                        .description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답데이터"),
                    fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("사용자 ID"),
                    fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("사용자 닉네임")
                ))
            );
    }

    @DisplayName("회원 로그아웃 API 문서화")
    @Test
    void logout() throws Exception {
        //given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("MEMBER", 1L);

        //when then
        mockMvc.perform(get("/api/v1/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
            .andExpect(status().isOk())
            .andDo(document("member-logout",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()))
            );
    }

    @DisplayName("회원 비밀번호 재설정 API 문서화")
    @Test
    void rePassword() throws Exception {
        //given
        MockHttpSession session = new MockHttpSession();
        RePasswordRequest dto = new RePasswordRequest("testPassword", "testPassword");
        MemberResponse memberResponse = new MemberResponse(1L, "땅땅띠라랑");

        session.setAttribute("MEMBER", 1L);
        when(memberService.rePassword(any(RePasswordRequest.class), anyLong())).thenReturn(
            memberResponse);
        String data = new ObjectMapper().writeValueAsString(dto);

        //when then
        mockMvc.perform(post("/api/v1/RePassword").contentType(MediaType.APPLICATION_JSON)
                .content(data)
                .session(session))
            .andExpect(status().isOk())
            .andDo(document("member-rePassword",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                        .attributes(key("constraints").value("Not Blank")),
                    fieldWithPath("rePassword").type(JsonFieldType.STRING).description("비밀번호 재확인")
                        .attributes(key("constraints").value("Not Blank"))),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).optional()
                        .description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답데이터"),
                    fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("사용자 ID"),
                    fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("사용자 닉네임")
                ))
            );
    }

    @DisplayName("회원 가입 API 문서화")
    @Test
    void join() throws Exception {
        //given
        MemberDto memberDto = new MemberDto("test@gmail.com", "testPassword", "testNickname");

        //when
        when(memberService.isEmailExistsInFcmember(any(String.class))).thenReturn(true);
        when(memberService.isEmailExistsInMember(any(String.class))).thenReturn(false);
        when(memberService.checkDuplicateNickname(any(String.class))).thenReturn(false);
        doNothing().when(memberService).save(any(MemberDto.class));

        String data = new ObjectMapper().writeValueAsString(memberDto);

        //then
        mockMvc.perform(post("/v1/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(data))
            .andExpect(status().isOk())
            .andDo(document("member-join",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("email").type(JsonFieldType.STRING).description("이메일")
                        .attributes(key("constraints").value("Not Blank")),
                    fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                        .attributes(key("constraints").value("Not Blank")),
                    fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임")
                        .attributes(key("constraints").value("Not Blank"))

                )
            ));
    }

    @DisplayName("회원 탈퇴 API 문서화")
    @Test
    void deleteMember() throws Exception {
        // given
        long memberId = 1L;

        // when, then
        mockMvc.perform(delete("/v1/delete")
                .param("id", String.valueOf(memberId))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("member-delete",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestParameters(
                    parameterWithName("id").description("삭제할 회원의 ID")
                )
            ));
    }

    @DisplayName("마이페이지 조회 API 테스트")
    @Test
    void testGetMyPageInfo() throws Exception {
        // 가짜 세션을 생성하고 사용자 정보를 세션에 넣어줍니다.
        MockHttpSession session = new MockHttpSession();
        Member member = new Member();
        member.setId(1L);
        member.setNickname("testUser");
        member.setEmail("test@example.com");
        member.setImage("profile.jpg");
        session.setAttribute("MEMBER", member);

        // GET /api/v1/mypage 요청을 수행하고 API 문서화를 위한 스니펫을 생성합니다.
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/mypage")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON))
            // HTTP 상태 코드가 200 OK인지 확인합니다.
            .andExpect(status().isOk())
            // 응답 본문이 JSON 형식인지 확인합니다.
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            // 응답 본문에 필요한 필드가 있는지 확인합니다.
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("사용자 정보를 성공적으로 조회하였습니다."))
            .andExpect(jsonPath("$.data.nickname").value("testUser"))
            .andExpect(jsonPath("$.data.email").value("test@example.com"))
            .andExpect(jsonPath("$.data.profileImageUrl").value("profile.jpg"))
            // API 문서화를 위한 스니펫을 생성합니다.
            .andDo(document("member-getMyPageInfo",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())));
    }
    @DisplayName("회원 정보 수정 API 문서화")
    @Test
    void updateMember() throws Exception {
        // 가짜 회원 정보 생성 (실제로는 회원을 생성하고 저장해야 함)
        Member member = new Member();
        member.setId(1L); // 회원 ID 설정
        member.setEmail("test@example.com");
        member.setNickname("oldNickname");
        member.setImage("oldImageURL");

        // 가짜 HttpSession 객체 생성
        HttpSession session = new MockHttpSession();
        session.setAttribute("member", member);

        // 수정할 회원 정보를 나타내는 JSON 문자열 생성
        String requestJson = "{\"nickname\":\"newNickname\",\"image\":\"newImageURL\"}";

        // MockMvc를 사용하여 API 엔드포인트 호출하고 API 문서화를 위한 스니펫을 생성합니다.
        mockMvc.perform(put("/v1/retouch-member")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .session((MockHttpSession) session))
            .andExpect(jsonPath("$.nickname").value("newNickname"))
            .andExpect(jsonPath("$.image").value("newImageURL"))
            .andExpect(jsonPath("$.email").value("test@example.com"))

            // API 문서화를 위한 스니펫을 생성합니다.
            .andDo(document("member-update",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())));
    }













}
