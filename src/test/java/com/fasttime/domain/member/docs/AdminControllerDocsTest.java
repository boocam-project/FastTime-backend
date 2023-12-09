package com.fasttime.domain.member.docs;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasttime.docs.RestDocsSupport;
import com.fasttime.domain.article.dto.service.response.ArticleResponse;
import com.fasttime.domain.article.dto.service.response.ArticlesResponse;
import com.fasttime.domain.article.service.usecase.ArticleCommandUseCase.ArticleCreateServiceRequest;
import com.fasttime.domain.member.controller.AdminController;
import com.fasttime.domain.member.service.AdminService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;


class AdminControllerDocsTest extends RestDocsSupport {

    private final AdminService adminService = mock(AdminService.class);

    @Override
    public Object initController() {
        return new AdminController(adminService);
    }

    @DisplayName("신고 게시글 목록 조회 API 문서화")
    @Test
    void reportedPostsSearch() throws Exception {
        //given
        when(adminService.findReportedPost(0))
            .thenReturn(List.of(
                ArticlesResponse.builder().id(1L).title("공 잘 패스하는법 알려줌!").likeCount(20).hateCount(1)
                    .nickname("패캠러").anonymity(false).createdAt(LocalDateTime.now())
                    .lastModifiedAt(LocalDateTime.now()).build(),
                ArticlesResponse.builder().id(2L).title("패스트캠퍼스를 아시나요?").likeCount(20).hateCount(5)
                    .nickname("패캠러123").anonymity(false).createdAt(LocalDateTime.now())
                    .lastModifiedAt(LocalDateTime.now()).build(),
                ArticlesResponse.builder().id(3L).title("공무원합격 패스는 ㅇㅇㅇ").likeCount(20).hateCount(3)
                    .nickname("패컴러1").anonymity(false).createdAt(LocalDateTime.now())
                    .lastModifiedAt(LocalDateTime.now()).build()
            ));

        //when, then
        mockMvc.perform(get("/api/v1/admin")
                .queryParam("page", "0")
            )
            .andExpect(status().isOk())
            .andDo(document("reportedPosts-search",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(parameterWithName("page").description("조회 페이지").optional()),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).optional()
                        .description("메시지"),
                    fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("게시글 식별자"),
                    fieldWithPath("data[].title").type(JsonFieldType.STRING).description("게시글 제목"),
                    fieldWithPath("data[].nickname").type(JsonFieldType.STRING)
                        .description("작성자 닉네임"),
                    fieldWithPath("data[].anonymity").type(JsonFieldType.BOOLEAN)
                        .description("익명 여부"),
                    fieldWithPath("data[].commentCounts").type(JsonFieldType.NUMBER)
                        .description("댓글 수"),
                    fieldWithPath("data[].likeCount").type(JsonFieldType.NUMBER)
                        .description("좋아요 수"),
                    fieldWithPath("data[].hateCount").type(JsonFieldType.NUMBER)
                        .description("싫어요 수"),
                    fieldWithPath("data[].createdAt").type(JsonFieldType.STRING)
                        .description("생성 일시"),
                    fieldWithPath("data[].lastModifiedAt").type(JsonFieldType.STRING)
                        .description("수정 일시")
                )));
    }

    @DisplayName("신고 게시글 상세 조회 API 문서화")
    @Test
    void ReportedPostDetailSearch() throws Exception {
        // given
        ArticleCreateServiceRequest requestDto = new ArticleCreateServiceRequest(1L, "게시글 제목입니다.",
            "게시글 본문입니다.", false);

        when(adminService.findOneReportedPost(anyLong()))
            .thenReturn(ArticleResponse.builder()
                .id(1L)
                .nickname("패캠러")
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .anonymity(requestDto.isAnonymity())
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build());

        // when then
        mockMvc.perform(get("/api/v1/admin/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("reportedPost-search",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("id").description("게시글 식별자")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).optional()
                        .description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답데이터"),
                    fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("게시글 식별자"),
                    fieldWithPath("data.title").type(JsonFieldType.STRING).description("게시글 제목"),
                    fieldWithPath("data.nickname").type(JsonFieldType.STRING)
                        .description("작성자 닉네임"),
                    fieldWithPath("data.content").type(JsonFieldType.STRING).description("게시글 본문"),
                    fieldWithPath("data.anonymity").type(JsonFieldType.BOOLEAN)
                        .description("익명 여부"),
                    fieldWithPath("data.likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                    fieldWithPath("data.hateCount").type(JsonFieldType.NUMBER).description("싫어요 수"),
                    fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 일시"),
                    fieldWithPath("data.lastModifiedAt").type(JsonFieldType.STRING)
                        .description("수정 일시"))));
    }

    @DisplayName("신고 게시글 삭제 API 문서화")
    @Test
    void deletePost() throws Exception {
        //given
        ArticleCreateServiceRequest requestDto = new ArticleCreateServiceRequest(1L, "게시글 제목입니다.",
            "게시글 본문입니다.", false);

        when(adminService.findOneReportedPost(anyLong()))
            .thenReturn(ArticleResponse.builder()
                .id(1L)
                .nickname("패캠러")
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .anonymity(requestDto.isAnonymity())
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build());
        doNothing().when(adminService).deletePost(1L);

        //when then
        mockMvc.perform(get("/api/v1/admin/{id}/delete", 1)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("reportedPost-delete",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    pathParameters(
                        parameterWithName("id").description("게시글 식별자"))));
    }

    @DisplayName("신고 게시글 복구 API 문서화")
    @Test
    void passPost() throws Exception {
        //given
        ArticleCreateServiceRequest requestDto = new ArticleCreateServiceRequest(1L, "게시글 제목입니다.",
            "게시글 본문입니다.", false);

        when(adminService.findOneReportedPost(anyLong()))
            .thenReturn(ArticleResponse.builder()
                .id(1L)
                .nickname("패캠러")
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .anonymity(requestDto.isAnonymity())
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build());
        doNothing().when(adminService).passPost(1L);

        //when then
        mockMvc.perform(get("/api/v1/admin/{id}/pass", 1)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("reportedPost-pass",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("id").description("게시글 식별자"))));
    }

//    @Disabled
//    @Test
//    void join() throws Exception {
        //given
//        MemberDto dto = MemberDto.builder()
//            .email("test@gmail.com")
//            .nickname("memberNickname")
//            .password("1234")
//            .build();
//        doNothing().when(adminService).save(any(MemberDto.class));
//        String data = new ObjectMapper().writeValueAsString(dto);
//
//        //when then
//        mockMvc.perform(post("/api/v1/admin/join")
//                .contentType(MediaType.APPLICATION_JSON).content(data))
//            .andExpect(status().isOk()).andDo(
//                document("admin-join", preprocessRequest(prettyPrint()),
//                    preprocessResponse(prettyPrint()), requestFields(
//                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일")
//                            .attributes(key("constraints").value("Not Blank")),
//                        fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
//                            .attributes(key("constraints").value("Not Blank")))));
//    }
}
