package com.fasttime.domain.member.docs;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
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
import com.fasttime.domain.member.dto.request.LoginRequestDTO;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.member.request.RePasswordRequest;
import com.fasttime.domain.member.response.MemberResponse;
import com.fasttime.domain.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.payload.JsonFieldType;

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
        LoginRequestDTO dto = new LoginRequestDTO("123@gmail.com","testPassword");
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
                    fieldWithPath("message").type(JsonFieldType.STRING).optional().description("메시지"),
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
        session.setAttribute("MEMBER",1L);

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
        RePasswordRequest dto = new RePasswordRequest("testPassword","testPassword");
        MemberResponse memberResponse = new MemberResponse(1L, "땅땅띠라랑");

        session.setAttribute("MEMBER",1L);
        when(memberService.rePassword(any(RePasswordRequest.class),anyLong())).thenReturn(memberResponse);
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
                    fieldWithPath("message").type(JsonFieldType.STRING).optional().description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답데이터"),
                    fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("사용자 ID"),
                    fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("사용자 닉네임")
                ))
            );
    }
}
