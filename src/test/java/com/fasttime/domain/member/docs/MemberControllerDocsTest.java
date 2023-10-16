package com.fasttime.domain.member.docs;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import com.fasttime.domain.member.response.EditResponse;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

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
        mockMvc.perform(post("/api/v1/login").contentType(MediaType.APPLICATION_JSON).content(data))
            .andExpect(status().isOk()).andDo(
                document("member-login", preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()), requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일")
                            .attributes(key("constraints").value("Not Blank")),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                            .attributes(key("constraints").value("Not Blank"))), responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).optional()
                            .description("메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답데이터"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("사용자 ID"),
                        fieldWithPath("data.nickname").type(JsonFieldType.STRING)
                            .description("사용자 닉네임"))));
    }

    @DisplayName("회원 로그아웃 API 문서화")
    @Test
    void logout() throws Exception {
        //given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("MEMBER", 1L);

        //when then
        mockMvc.perform(
                get("/api/v1/logout").contentType(MediaType.APPLICATION_JSON).session(session))
            .andExpect(status().isOk()).andDo(
                document("member-logout", preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint())));
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
        mockMvc.perform(
            post("/api/v1/RePassword").contentType(MediaType.APPLICATION_JSON).content(data)
                .session(session)).andExpect(status().isOk()).andDo(
            document("member-rePassword", preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()), requestFields(
                    fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                        .attributes(key("constraints").value("Not Blank")),
                    fieldWithPath("rePassword").type(JsonFieldType.STRING).description("비밀번호 재확인")
                        .attributes(key("constraints").value("Not Blank"))), responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).optional()
                        .description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답데이터"),
                    fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("사용자 ID"),
                    fieldWithPath("data.nickname").type(JsonFieldType.STRING)
                        .description("사용자 닉네임"))));
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
        mockMvc.perform(post("/v1/join").contentType(MediaType.APPLICATION_JSON).content(data))
            .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200)) // 상태 코드 200 확인
            .andExpect(jsonPath("$.message").value("가입 성공!")) // 메시지 확인
            .andExpect(jsonPath("$.data").value("가입 성공!")) // 응답 데이터 확인
            .andDo(document("member-join", preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()), requestFields(
                    fieldWithPath("email").type(JsonFieldType.STRING).description("이메일")
                        .attributes(key("constraints").value("Not Blank")),
                    fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                        .attributes(key("constraints").value("Not Blank")),
                    fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임")
                        .attributes(key("constraints").value("Not Blank"))), responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).optional()
                        .description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.STRING).optional()
                        .description("응답데이터"))));
    }

    @DisplayName("회원 탈퇴 API 문서화")
    @Test
    void deleteMember() throws Exception {
        // given
        long memberId = 1L;
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("MEMBER", memberId);

        // when, then
        mockMvc.perform(
                delete("/v1/delete").session(session).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200)) // 상태 코드 200 확인
            .andExpect(jsonPath("$.message").value("탈퇴가 완료되었습니다.")) // 메시지 확인
            .andDo(document("member-delete", preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())));
    }

    @DisplayName("마이페이지 조회 API 문서화")
    @Test
    void testGetMyPageInfo() throws Exception {
        // Given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("MEMBER", 1L);

        Member member = new Member();
        member.setId(1L);
        member.setNickname("NewNickname");
        member.setEmail("test@example.com");
        member.setImage("newimageURL");

        when(memberService.getMember(1L)).thenReturn(member);

        // When
        ResultActions result = mockMvc.perform(get("/api/v1/mypage").session(session)
            .contentType(MediaType.APPLICATION_JSON_UTF8));

        // Then
        result.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("사용자 정보를 성공적으로 조회하였습니다."))
            .andExpect(jsonPath("$.data.nickname").value("NewNickname"))
            .andExpect(jsonPath("$.data.email").value("test@example.com"))
            .andExpect(jsonPath("$.data.profileImageUrl").value("newimageURL"))
            .andDo(document("member-getMyPageInfo",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").description("응답 상태 코드").type(JsonFieldType.NUMBER),
                    fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                    fieldWithPath("data.nickname").description("사용자 닉네임")
                        .type(JsonFieldType.STRING),
                    fieldWithPath("data.email").description("사용자 이메일").type(JsonFieldType.STRING),
                    fieldWithPath("data.profileImageUrl").description("프로필 이미지 URL")
                        .type(JsonFieldType.STRING)
                )
            ));
    }


    @DisplayName("회원 정보 수정 API 문서화")
    @Test
    void updateMember() throws Exception {
        // Given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("MEMBER", 1L);

        EditRequest editRequest = new EditRequest();
        editRequest.setNickname("NewNickname");
        editRequest.setImage("new-image-url");

        Member member = new Member();
        member.setId(1L);
        member.setNickname("OldNickname");
        member.setImage("old-image-url");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(memberService.checkDuplicateNickname("NewNickname")).thenReturn(false);

        // When
        ResultActions result = mockMvc.perform(
            put("/v1/retouch-member").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editRequest)).session(session));

        // Then
        result.andExpect(status().isOk())
            .andDo(document("member-update",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").description("응답 상태 코드").type(JsonFieldType.NUMBER),
                    fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                    fieldWithPath("data.email").description("변경된 이메일").type(JsonFieldType.STRING)
                        .optional(),
                    fieldWithPath("data.nickname").description("변경된 닉네임")
                        .type(JsonFieldType.STRING),
                    fieldWithPath("data.image").description("변경된 이미지 URL")
                        .type(JsonFieldType.STRING),
                    fieldWithPath("data.message").description("변경된 메시지").type(JsonFieldType.NULL)
                        .optional()
                )
            ));


    }

}
