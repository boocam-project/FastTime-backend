package com.fasttime.domain.article.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasttime.docs.RestDocsSupport;
import com.fasttime.domain.article.controller.ArticleControllerV2;
import com.fasttime.domain.article.dto.controller.request.ArticleCreateRequest;
import com.fasttime.domain.article.dto.controller.request.ArticleUpdateRequestV2;
import com.fasttime.domain.article.dto.service.response.ArticleResponse;
import com.fasttime.domain.article.dto.service.response.ArticlesResponse;
import com.fasttime.domain.article.service.usecase.ArticleCommandUseCase;
import com.fasttime.domain.article.service.usecase.ArticleCommandUseCase.ArticleCreateServiceRequest;
import com.fasttime.domain.article.service.usecase.ArticleCommandUseCase.ArticleDeleteServiceRequest;
import com.fasttime.domain.article.service.usecase.ArticleCommandUseCase.ArticleUpdateServiceRequest;
import com.fasttime.domain.article.service.usecase.ArticleQueryUseCase;
import com.fasttime.domain.article.service.usecase.ArticleQueryUseCase.ArticlesSearchRequestServiceDto;
import com.fasttime.global.util.SecurityUtil;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.snippet.Attributes.Attribute;

class ArticleControllerV2DocsTest extends RestDocsSupport {

    private final ArticleQueryUseCase articleQueryUseCase = mock(ArticleQueryUseCase.class);
    private final ArticleCommandUseCase articleCommandUseCase = mock(ArticleCommandUseCase.class);
    private final SecurityUtil securityUtil = mock(SecurityUtil.class);

    @Override
    public Object initController() {
        return new ArticleControllerV2(articleCommandUseCase, articleQueryUseCase, securityUtil);
    }

    @DisplayName("게시글 작성 API V2 문서화")
    @Test
    void createArticle() throws Exception {

        // given
        ArticleCreateRequest requestDto = new ArticleCreateRequest("게시글 제목입니다.",
            "게시글 본문입니다.", false);

        when(articleCommandUseCase.write(any(ArticleCreateServiceRequest.class)))
            .thenReturn(ArticleResponse.builder()
                .id(1L)
                .nickname("패캠러")
                .title(requestDto.title())
                .content(requestDto.content())
                .isAnonymity(requestDto.isAnonymity())
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(null)
                .build());

        // when then
        mockMvc.perform(post("/api/v2/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isCreated())
            .andDo(document("articles/v2/create",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("title").type(JsonFieldType.STRING).description("제목")
                        .attributes(key("constraints").value("50자 이하")),
                    fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                    fieldWithPath("isAnonymity").type(JsonFieldType.BOOLEAN).description("익명여부")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).optional()
                        .description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.STRING).description("생성된 게시글의 조회 URL")
                )
            ));
    }

    @DisplayName("게시글 수정 API V2 문서화")
    @Test
    void updateArticle() throws Exception {

        // given
        ArticleUpdateRequestV2 request = new ArticleUpdateRequestV2(
            "새로운 게시글 제목입니다.",
            "새로운 게시글 본문입니다.",
            true);

        when(articleCommandUseCase.update(any(ArticleUpdateServiceRequest.class)))
            .thenReturn(ArticleResponse.builder()
                .id(1L)
                .nickname("패캠러")
                .title(request.title())
                .content(request.content())
                .isAnonymity(false)
                .likeCount(5)
                .hateCount(1)
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build());

        // when then
        mockMvc.perform(put("/api/v2/articles/{article_id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andDo(document("articles/v2/update",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("article_id").description("수정할 게시글 식별자")),
                requestFields(
                    fieldWithPath("title").type(JsonFieldType.STRING).description("제목")
                        .attributes(new Attribute("constraints", "50자 이하")),
                    fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                    fieldWithPath("isAnonymity").type(JsonFieldType.BOOLEAN).description("익명여부")
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
                    fieldWithPath("data.isAnonymity").type(JsonFieldType.BOOLEAN)
                        .description("익명 여부"),
                    fieldWithPath("data.likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                    fieldWithPath("data.hateCount").type(JsonFieldType.NUMBER).description("싫어요 수"),
                    fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 일시"),
                    fieldWithPath("data.lastModifiedAt").type(JsonFieldType.STRING)
                        .description("수정 일시")
                )
            ));
    }

    @DisplayName("게시글 삭제 API V2 문서화")
    @Test
    void deleteArticle() throws Exception {

        // given
        doNothing().when(articleCommandUseCase).delete(any(ArticleDeleteServiceRequest.class));

        // when then
        mockMvc.perform(delete("/api/v2/articles/{article_id}", 1))
            .andExpect(status().isOk())
            .andDo(document("articles/v2/delete",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("article_id").description("삭제할 게시글 식별자"))
            ));
    }

    @DisplayName("게시글 목록 조회 API 문서화")
    @Test
    void searchArticles() throws Exception {

        // given
        ArticlesSearchRequestServiceDto.builder()
            .title("패스")
            .nickname("패캠러")
            .likeCount(10)
            .pageSize(10)
            .page(0)
            .build();

        when(articleQueryUseCase.search(any(ArticlesSearchRequestServiceDto.class)))
            .thenReturn(List.of(
                ArticlesResponse.builder().id(1L).title("공 잘 패스하는법 알려줌!").likeCount(20).hateCount(1)
                    .nickname("패캠러").isAnonymity(false).createdAt(LocalDateTime.now())
                    .lastModifiedAt(LocalDateTime.now()).build(),
                ArticlesResponse.builder().id(2L).title("패스트캠퍼스를 아시나요?").likeCount(20).hateCount(5)
                    .nickname("패캠러123").isAnonymity(false).createdAt(LocalDateTime.now())
                    .lastModifiedAt(LocalDateTime.now()).build(),
                ArticlesResponse.builder().id(3L).title("공무원합격 패스는 ㅇㅇㅇ").likeCount(20).hateCount(3)
                    .nickname("패컴러1").isAnonymity(false).createdAt(LocalDateTime.now())
                    .lastModifiedAt(LocalDateTime.now()).build()
            ));

        // when then
        mockMvc.perform(get("/api/v2/articles")
                .queryParam("title", "패스")
                .queryParam("nickname", "패캠러")
                .queryParam("likeCount", "10")
                .queryParam("page", "0")
                .queryParam("pageSize", "10")
                .queryParam("orderBy", "likeCount")
                .queryParam("isAscending", "true")
            )
            .andExpect(status().isOk())
            .andDo(document("articles/v2/search",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(
                    parameterWithName("title").description("제목").optional(),
                    parameterWithName("nickname").description("작성자 닉네임").optional(),
                    parameterWithName("likeCount").description("최소 좋아요 수").optional()
                        .attributes(new Attribute("constraints", "0보다 커야 합니다.")),
                    parameterWithName("pageSize").description("조회당 불러올 건 수").optional()
                        .attributes(new Attribute("defaultValue", "10")),
                    parameterWithName("orderBy").description(
                            "정렬 기준 : likeCount, commentCount, createdAt(default)").optional()
                        .attributes(new Attribute("constraints",
                                "likeCount : 좋아요 순\n commentCount : 댓글 갯수 순\n createdAt : 작성일 순"),
                            new Attribute("defaultValue", "createdAt")),
                    parameterWithName("isAscending").description("정렬 순서 (오름차순 or 내림차순)").optional()
                        .attributes(new Attribute("constraints", "true : 오름차순\n false : 내림차순"),
                            new Attribute("defaultValue", "false")),
                    parameterWithName("page").description("조회 페이지").optional()
                        .attributes(new Attribute("defaultValue", "0"))
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).optional()
                        .description("메시지"),
                    fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("게시글 식별자"),
                    fieldWithPath("data[].title").type(JsonFieldType.STRING).description("게시글 제목"),
                    fieldWithPath("data[].nickname").type(JsonFieldType.STRING)
                        .description("작성자 닉네임"),
                    fieldWithPath("data[].isAnonymity").type(JsonFieldType.BOOLEAN)
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
                )
            ));
    }

    @DisplayName("게시글 상세 조회 API 문서화")
    @Test
    void searchArticle() throws Exception {

        // given
        ArticleCreateServiceRequest requestDto = new ArticleCreateServiceRequest(1L, "게시글 제목입니다.",
            "게시글 본문입니다.", false);

        when(articleQueryUseCase.queryById(anyLong()))
            .thenReturn(ArticleResponse.builder()
                .id(1L)
                .nickname("패캠러")
                .title(requestDto.title())
                .content(requestDto.content())
                .isAnonymity(requestDto.isAnonymity())
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build());

        // when then
        mockMvc.perform(get("/api/v2/articles/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("articles/v2/get-detail",
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
                    fieldWithPath("data.isAnonymity").type(JsonFieldType.BOOLEAN)
                        .description("익명 여부"),
                    fieldWithPath("data.likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                    fieldWithPath("data.hateCount").type(JsonFieldType.NUMBER).description("싫어요 수"),
                    fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 일시"),
                    fieldWithPath("data.lastModifiedAt").type(JsonFieldType.STRING)
                        .description("수정 일시")
                )
            ));
    }
}
