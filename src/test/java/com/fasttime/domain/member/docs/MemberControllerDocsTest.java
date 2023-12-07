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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.snippet.Attributes.key;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasttime.docs.RestDocsSupport;
import com.fasttime.domain.member.controller.MemberController;
import com.fasttime.domain.member.dto.request.CreateMemberDTO;
import com.fasttime.domain.member.dto.request.LoginRequestDTO;
import com.fasttime.domain.member.dto.request.MyPageInfoDTO;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.member.dto.request.EditRequest;
import com.fasttime.domain.member.dto.request.RePasswordRequest;
import com.fasttime.domain.member.dto.response.MemberResponse;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.global.exception.ErrorCode;
import com.fasttime.global.util.ResponseDTO;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;


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
        CreateMemberDTO createMemberDTO = new CreateMemberDTO("test@gmail.com", "testPassword", "testNickname");

        //when
        when(memberService.registerOrRecoverMember(any(CreateMemberDTO.class)))
            .thenReturn(ResponseDTO.res(HttpStatus.OK, "가입 성공!"));

        String data = new ObjectMapper().writeValueAsString(createMemberDTO);

        //then
        mockMvc.perform(post("/api/v1/join").contentType(MediaType.APPLICATION_JSON).content(data))
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
        CreateMemberDTO createMemberDTO = new CreateMemberDTO("test@gmail.com", "testPassword", "testNickname");

        //when
        when(memberService.registerOrRecoverMember(any(CreateMemberDTO.class)))
            .thenReturn(ResponseDTO.res(HttpStatus.OK, "계정이 성공적으로 복구되었습니다!"));

        String data = new ObjectMapper().writeValueAsString(createMemberDTO);

        //then
        mockMvc.perform(post("/api/v1/join").contentType(MediaType.APPLICATION_JSON).content(data))
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
                delete("/api/v1/delete").session(session).contentType(MediaType.APPLICATION_JSON))
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

        MyPageInfoDTO myPageInfoDTO = new MyPageInfoDTO("NewNickname", "newimageURL",
            "test@example.com");
        when(memberService.getMyPageInfoById(1L)).thenReturn(myPageInfoDTO);

        // When
        ResultActions result = mockMvc.perform(get("/api/v1/mypage").session(session)
            .contentType(MediaType.APPLICATION_JSON));

        // Then
        result.andExpect(status().isOk())
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(
                jsonPath("$.message").value(ErrorCode.MY_PAGE_RETRIEVED_SUCCESS.getMessage()))
            .andExpect(jsonPath("$.data.nickname").value("NewNickname"))
            .andExpect(jsonPath("$.data.image").value("newimageURL"))
            .andExpect(jsonPath("$.data.email").value("test@example.com"))
            .andDo(document("member-getMyPageInfo",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").description("응답 상태 코드").type(JsonFieldType.NUMBER),
                    fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                    fieldWithPath("data.nickname").description("사용자 닉네임")
                        .type(JsonFieldType.STRING),
                    fieldWithPath("data.image").description("프로필 이미지 URL")
                        .type(JsonFieldType.STRING),
                    fieldWithPath("data.email").description("사용자 이메일").type(JsonFieldType.STRING)
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
        member.setNickname("NewNickname");
        member.setImage("new-image-url");

        member.setEmail(null);

        when(memberService.updateMemberInfo(any(EditRequest.class), any(HttpSession.class)))
            .thenReturn(Optional.of(member));

        // When
        ResultActions result = mockMvc.perform(
            put("/api/v1/retouch-member")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editRequest))
                .session(session));

        // Then
        result.andExpect(status().isOk())
            .andExpect(
                jsonPath("$.code").value(ErrorCode.MEMBER_UPDATE_SUCCESS.getHttpStatus().value()))
            .andExpect(jsonPath("$.message").value(ErrorCode.MEMBER_UPDATE_SUCCESS.getMessage()))
            .andExpect(jsonPath("$.data.nickname").value("NewNickname"))
            .andExpect(jsonPath("$.data.image").value("new-image-url"))
            .andExpect(
                jsonPath("$.data.email").doesNotExist()) // Expect email to not exist if it's null
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

                    fieldWithPath("data.email").description("이메일 (변경되지 않았을 수 있음)")
                        .type(JsonFieldType.STRING).optional()
                )
            ));
    }


}
