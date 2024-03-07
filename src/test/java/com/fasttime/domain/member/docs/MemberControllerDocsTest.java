package com.fasttime.domain.member.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasttime.docs.RestDocsSupport;
import com.fasttime.domain.member.controller.MemberController;
import com.fasttime.domain.member.dto.request.CreateMemberRequest;
import com.fasttime.domain.member.dto.request.UpdateMemberRequest;
import com.fasttime.domain.member.dto.request.LoginRequest;
import com.fasttime.domain.member.dto.request.RePasswordRequest;
import com.fasttime.domain.member.dto.request.RefreshRequest;
import com.fasttime.domain.member.dto.response.LoginResponse;
import com.fasttime.domain.member.dto.response.RepasswordResponse;
import com.fasttime.domain.member.dto.response.RefreshResponse;
import com.fasttime.domain.member.dto.response.GetMyInfoResponse;
import com.fasttime.domain.member.dto.response.TokenResponse;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.global.util.ResponseDTO;
import com.fasttime.global.util.SecurityUtil;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

class MemberControllerDocsTest extends RestDocsSupport {

    private final MemberService memberService = mock(MemberService.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final SecurityUtil securityUtil = mock(SecurityUtil.class);

    @Override
    public Object initController() {
        return new MemberController(memberService, memberRepository, securityUtil);
    }


    @Test
    @DisplayName("회원 로그인 API 문서화")
    void login() throws Exception {
        // given
        LoginRequest loginRequest = LoginRequest.builder()
            .email("test@mail.com")
            .password("qwer1234$$")
            .build();
        RefreshResponse refreshResponse = RefreshResponse.builder()
            .memberId(1L)
            .email("test@mail.com")
            .nickname("test")
            .image("")
            .build();
        TokenResponse tokenResponse = TokenResponse.builder()
            .grantType("Bearer")
            .accessToken(
                "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTcwMDU4NjkyOH0.lof7WjalCH1gGPy2q7YYi9VTcgn_aoFMwEMQvITtddsUIcJN-YzNODt_RQde5J5dH98NKMXDOvy7YwNlt6BCfg")
            .accessTokenExpiresIn(1700586928520L)
            .refreshToken(
                "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE3MDExODk5Mjh9.uZuIAxsnf4Ubz5K9YzysJTu9Gh25XNTsPVAPSElw1lS78gS8S08L97Z4RkfGodegGXZ9UFFNkVXdhRzF9Pr-uA")
            .build();
        LoginResponse logInResponse = LoginResponse.builder()
            .member(refreshResponse)
            .token(tokenResponse)
            .build();

        given(memberService.loginMember(any(LoginRequest.class))).willReturn(logInResponse);

        // when
        mockMvc.perform(post("/api/v2/login")
                .content(objectMapper.writeValueAsString(loginRequest))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("member-login", preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()), requestFields(
                    fieldWithPath("email").type(JsonFieldType.STRING).description("이메일")
                        .attributes(key("constraints").value("Not Blank")),
                    fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                        .attributes(key("constraints").value("Not Blank"))), responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).optional()
                        .description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                    fieldWithPath("data.member").type(JsonFieldType.OBJECT).description("회원 정보"),
                    fieldWithPath("data.member.memberId").type(JsonFieldType.NUMBER)
                        .description("회원 식별자"),
                    fieldWithPath("data.member.email").type(JsonFieldType.STRING)
                        .description("이메일"),
                    fieldWithPath("data.member.nickname").type(JsonFieldType.STRING)
                        .description("이름"),
                    fieldWithPath("data.member.image").type(JsonFieldType.STRING)
                        .description("프로필 이미지"),
                    fieldWithPath("data.token").type(JsonFieldType.OBJECT).description("토큰 정보"),
                    fieldWithPath("data.token.grantType").type(JsonFieldType.STRING)
                        .description("권한 부여 유형"),
                    fieldWithPath("data.token.accessToken").type(JsonFieldType.STRING)
                        .description("Access Token"),
                    fieldWithPath("data.token.accessTokenExpiresIn").type(JsonFieldType.NUMBER)
                        .description("Access Token 만료 날짜"),
                    fieldWithPath("data.token.refreshToken").type(JsonFieldType.STRING)
                        .description("Refresh Token")
                )
            ));

    }

    @Test
    @DisplayName("토큰 재발급 API 문서화")
    void refresh() throws Exception {
        // given
        RefreshRequest refreshRequest = RefreshRequest.builder()
            .accessToken(
                "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTcwMDU4NjkyOH0.lof7WjalCH1gGPy2q7YYi9VTcgn_aoFMwEMQvITtddsUIcJN-YzNODt_RQde5J5dH98NKMXDOvy7YwNlt6BCfg")
            .refreshToken(
                "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE3MDExODk5Mjh9.uZuIAxsnf4Ubz5K9YzysJTu9Gh25XNTsPVAPSElw1lS78gS8S08L97Z4RkfGodegGXZ9UFFNkVXdhRzF9Pr-uA")
            .build();
        RefreshResponse refreshResponse = RefreshResponse.builder()
            .memberId(1L)
            .email("test@mail.com")
            .nickname("test")
            .image("")
            .build();
        TokenResponse tokenResponse = TokenResponse.builder()
            .grantType("Bearer")
            .accessToken(
                "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTcwMDU4NjkyOH0.lof7WjalCH1gGPy2q7YYi9VTcgn_aoFMwEMQvITtddsUIcJN-YzNODt_RQde5J5dH98NKMXDOvy7YwNlt6BCfg")
            .accessTokenExpiresIn(1700586928520L)
            .refreshToken(
                "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE3MDExODk5Mjh9.uZuIAxsnf4Ubz5K9YzysJTu9Gh25XNTsPVAPSElw1lS78gS8S08L97Z4RkfGodegGXZ9UFFNkVXdhRzF9Pr-uA")
            .build();
        LoginResponse logInResponse = LoginResponse.builder()
            .member(refreshResponse)
            .token(tokenResponse)
            .build();

        given(memberService.refresh(any(RefreshRequest.class))).willReturn(logInResponse);

        // when
        mockMvc.perform(post("/api/v2/refresh")
                .content(objectMapper.writeValueAsString(refreshRequest))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("member-refresh", preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()), requestFields(
                    fieldWithPath("accessToken").type(JsonFieldType.STRING)
                        .description("Access Token")
                        .attributes(key("constraints").value("Not Blank")),
                    fieldWithPath("refreshToken").type(JsonFieldType.STRING)
                        .description("Refresh Token")
                        .attributes(key("constraints").value("Not Blank"))), responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).optional()
                        .description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                    fieldWithPath("data.member").type(JsonFieldType.OBJECT).description("회원 정보"),
                    fieldWithPath("data.member.memberId").type(JsonFieldType.NUMBER)
                        .description("회원 식별자"),
                    fieldWithPath("data.member.email").type(JsonFieldType.STRING)
                        .description("이메일"),
                    fieldWithPath("data.member.nickname").type(JsonFieldType.STRING)
                        .description("이름"),
                    fieldWithPath("data.member.image").type(JsonFieldType.STRING)
                        .description("프로필 이미지"),
                    fieldWithPath("data.token").type(JsonFieldType.OBJECT).description("토큰 정보"),
                    fieldWithPath("data.token.grantType").type(JsonFieldType.STRING)
                        .description("권한 부여 유형"),
                    fieldWithPath("data.token.accessToken").type(JsonFieldType.STRING)
                        .description("Access Token"),
                    fieldWithPath("data.token.accessTokenExpiresIn").type(JsonFieldType.NUMBER)
                        .description("Access Token 만료 날짜"),
                    fieldWithPath("data.token.refreshToken").type(JsonFieldType.STRING)
                        .description("Refresh Token")
                )
            ));

    }


    @DisplayName("회원 비밀번호 재설정 API 문서화")
    @Test
    void rePassword() throws Exception {
        //given
        MockHttpSession session = new MockHttpSession();
        RePasswordRequest dto = new RePasswordRequest("testPassword", "testPassword");
        RepasswordResponse repasswordResponse = new RepasswordResponse(1L, "땅땅띠라랑");

        session.setAttribute("MEMBER", 1L);
        when(memberService.rePassword(any(RePasswordRequest.class), anyLong())).thenReturn(
            repasswordResponse);
        String data = new ObjectMapper().writeValueAsString(dto);

        // when then
        mockMvc
            .perform(
                post("/api/v1/members/me/password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(data)
                    .session(session))
            .andExpect(status().isOk())
            .andDo(
                document(
                    "member-rePassword",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("password")
                            .type(JsonFieldType.STRING)
                            .description("비밀번호")
                            .attributes(key("constraints").value("Not Blank")),
                        fieldWithPath("rePassword")
                            .type(JsonFieldType.STRING)
                            .description("비밀번호 재확인")
                            .attributes(key("constraints").value("Not Blank"))),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                        fieldWithPath("message")
                            .type(JsonFieldType.STRING)
                            .optional()
                            .description("메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답데이터"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("사용자 ID"),
                        fieldWithPath("data.nickname")
                            .type(JsonFieldType.STRING)
                            .description("사용자 닉네임"))));
    }

    @DisplayName("회원 가입 API 문서화")
    @Test
    void join() throws Exception {
        //given
        CreateMemberRequest createMemberRequest = new CreateMemberRequest("test@gmail.com",
            "testPassword",
            "testNickname");

        //when
        when(memberService.registerOrRecoverMember(any(CreateMemberRequest.class)))
            .thenReturn(ResponseDTO.res(HttpStatus.OK, "가입 성공!"));

        String data = new ObjectMapper().writeValueAsString(createMemberRequest);

        //then
        mockMvc.perform(
                post("/api/v1/members").contentType(MediaType.APPLICATION_JSON).content(data))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("가입 성공!"))
            .andExpect(jsonPath("$.data").doesNotExist())
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


    @DisplayName("회원 복구 API 문서화")
    @Test
    void recover() throws Exception {
        //given
        CreateMemberRequest createMemberRequest = new CreateMemberRequest("test@gmail.com",
            "testPassword",
            "testNickname");

        //when
        when(memberService.registerOrRecoverMember(any(CreateMemberRequest.class)))
            .thenReturn(ResponseDTO.res(HttpStatus.OK, "계정이 성공적으로 복구되었습니다!"));

        String data = new ObjectMapper().writeValueAsString(createMemberRequest);

        //then
        mockMvc.perform(
                post("/api/v1/members").contentType(MediaType.APPLICATION_JSON).content(data))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("계정이 성공적으로 복구되었습니다!"))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andDo(document("member-restore", preprocessRequest(prettyPrint()),
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
                delete("/api/v1/members/me").session(session).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("탈퇴가 완료되었습니다."))
            .andDo(document("member-delete", preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())));
    }


    @DisplayName("마이페이지 조회 API 문서화")
    @Test
    void testGetMyPageInfo() throws Exception {
        // Given
        Long expectedMemberId = 1L;
        GetMyInfoResponse getMyInfoResponse = new GetMyInfoResponse("NewNickname", "newimageURL",
            "test@example.com", true, "패스트캠퍼스x야놀자 부트캠프");

        when(securityUtil.getCurrentMemberId()).thenReturn(expectedMemberId);
        when(memberService.getMyPageInfoById(expectedMemberId)).thenReturn(getMyInfoResponse);

        // When
        ResultActions result = mockMvc.perform(get("/api/v1/members/me/page")
            .contentType(MediaType.APPLICATION_JSON));

        // Then
        result.andExpect(status().isOk())
            .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("사용자 정보를 성공적으로 조회하였습니다."))
            .andExpect(jsonPath("$.data.nickname").value("NewNickname"))
            .andExpect(jsonPath("$.data.image").value("newimageURL"))
            .andExpect(jsonPath("$.data.email").value("test@example.com"))
            .andExpect(jsonPath("$.data.campCrtfc").value(true))
            .andExpect(jsonPath("$.data.bootcampName").value("패스트캠퍼스x야놀자 부트캠프"))
            .andDo(document("member-getMyPageInfo",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").description("응답 상태 코드"),
                    fieldWithPath("message").description("응답 메시지"),
                    fieldWithPath("data.nickname").description("사용자 닉네임"),
                    fieldWithPath("data.image").description("프로필 이미지 URL"),
                    fieldWithPath("data.email").description("사용자 이메일"),
                    fieldWithPath("data.campCrtfc").description("부트캠프 인증여부"),
                    fieldWithPath("data.bootcampName").description("부트캠프 이름")
                )
            ));
    }


    @DisplayName("회원 정보 수정 API 테스트")
    @Test
    void updateMember() throws Exception {
        // Given
        Long expectedMemberId = 1L;
        UpdateMemberRequest updateMemberRequest = new UpdateMemberRequest("NewNickname",
            "new-image-url");
        Member updatedMember = Member.builder()
            .id(expectedMemberId)
            .nickname("NewNickname")
            .image("new-image-url")
            .email("test@example.com")
            .build();

        when(securityUtil.getCurrentMemberId()).thenReturn(expectedMemberId);
        when(memberService.updateMemberInfo(any(UpdateMemberRequest.class), eq(expectedMemberId)))
            .thenReturn(Optional.of(updatedMember));

        // When
        ResultActions result = mockMvc.perform(
            put("/api/v1/members/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateMemberRequest)));

        // Then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.data.nickname").value("NewNickname"))
            .andExpect(jsonPath("$.data.image").value("new-image-url"))
            .andExpect(jsonPath("$.data.email").value("test@example.com"))
            .andDo(document("member-update",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").description("응답 상태 코드").type(JsonFieldType.NUMBER),
                    fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                    fieldWithPath("data.nickname").description("변경된 닉네임")
                        .type(JsonFieldType.STRING),
                    fieldWithPath("data.image").description("변경된 이미지 URL")
                        .type(JsonFieldType.STRING),
                    fieldWithPath("data.email").description("사용자 이메일").type(JsonFieldType.STRING)
                )
            ));
    }


}