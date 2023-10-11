package com.fasttime.domain.post.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasttime.docs.RestDocsSupport;
import com.fasttime.domain.post.controller.PostController;
import com.fasttime.domain.post.dto.controller.request.PostDeleteRequestDto;
import com.fasttime.domain.post.dto.service.request.PostCreateServiceDto;
import com.fasttime.domain.post.dto.service.request.PostDeleteServiceDto;
import com.fasttime.domain.post.dto.service.request.PostUpdateServiceDto;
import com.fasttime.domain.post.dto.service.response.PostDetailResponseDto;
import com.fasttime.domain.post.dto.service.response.PostsResponseDto;
import com.fasttime.domain.post.service.PostCommandUseCase;
import com.fasttime.domain.post.service.PostQueryUseCase;
import com.fasttime.domain.post.service.PostQueryUseCase.PostSearchCondition;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.snippet.Attributes.Attribute;

class PostControllerDocsTest extends RestDocsSupport {

    private final PostQueryUseCase postQueryUseCase = mock(PostQueryUseCase.class);
    private final PostCommandUseCase postCommandUseCase = mock(PostCommandUseCase.class);

    @Override
    public Object initController() {
        return new PostController(postCommandUseCase, postQueryUseCase);
    }

    @DisplayName("게시글 작성 API 문서화")
    @Test
    void postWrite() throws Exception {

        // given
        PostCreateServiceDto requestDto = new PostCreateServiceDto(1L, "게시글 제목입니다.",
            "게시글 본문입니다.", false);

        when(postCommandUseCase.writePost(any(PostCreateServiceDto.class)))
            .thenReturn(PostDetailResponseDto.builder()
                .id(1L)
                .nickname("패캠러")
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .anonymity(requestDto.isAnonymity())
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(null)
                .build());

        // when then
        mockMvc.perform(post("/api/v1/post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isCreated())
            .andDo(document("post-create",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                    fieldWithPath("title").type(JsonFieldType.STRING).description("제목")
                        .attributes(key("constraints").value("50자 이하")),
                    fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                    fieldWithPath("anonymity").type(JsonFieldType.BOOLEAN).description("익명여부")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).optional().description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답데이터"),
                    fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("게시글 식별자"),
                    fieldWithPath("data.title").type(JsonFieldType.STRING).description("게시글 제목"),
                    fieldWithPath("data.content").type(JsonFieldType.STRING).description("게시글 본문"),
                    fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
                    fieldWithPath("data.anonymity").type(JsonFieldType.BOOLEAN).description("익명 여부"),
                    fieldWithPath("data.likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                    fieldWithPath("data.hateCount").type(JsonFieldType.NUMBER).description("싫어요 수"),
                    fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 일시"),
                    fieldWithPath("data.lastModifiedAt").type(JsonFieldType.NULL).description("최종 수정 일시")
                )
            ));
    }

    @DisplayName("게시글 목록 조회 API 문서화")
    @Test
    void postsSearch() throws Exception {

        // given
        PostSearchCondition.builder()
            .title("패스")
            .nickname("패캠러")
            .likeCount(10)
            .pageSize(10)
            .page(0)
            .build();

        when(postQueryUseCase.searchPost(any(PostSearchCondition.class)))
            .thenReturn(List.of(
                PostsResponseDto.builder().id(1L).title("공 잘 패스하는법 알려줌!").likeCount(20).hateCount(1)
                    .nickname("패캠러").anonymity(false).createdAt(LocalDateTime.now()).lastModifiedAt(LocalDateTime.now()).build(),
                PostsResponseDto.builder().id(2L).title("패스트캠퍼스를 아시나요?").likeCount(20).hateCount(5)
                    .nickname("패캠러123").anonymity(false).createdAt(LocalDateTime.now()).lastModifiedAt(LocalDateTime.now()).build(),
                PostsResponseDto.builder().id(3L).title("공무원합격 패스는 ㅇㅇㅇ").likeCount(20).hateCount(3)
                    .nickname("패컴러1").anonymity(false).createdAt(LocalDateTime.now()).lastModifiedAt(LocalDateTime.now()).build()
            ));

        // when then
        mockMvc.perform(get("/api/v1/post")
                .queryParam("title", "패스")
                .queryParam("nickname", "패캠러")
                .queryParam("likeCount", "10")
                .queryParam("page", "0")
                .queryParam("pageSize", "10")
            )
            .andExpect(status().isOk())
            .andDo(document("posts-search",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestParameters(
                    parameterWithName("title").description("제목").optional(),
                    parameterWithName("nickname").description("작성자 닉네임").optional(),
                    parameterWithName("likeCount").description("최소 좋아요 수").optional()
                        .attributes(new Attribute("constraints", "0보다 커야 합니다.")),
                    parameterWithName("pageSize").description("조회당 불러올 건 수").optional(),
                    parameterWithName("page").description("조회 페이지").optional()
                ),
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
                    fieldWithPath("data[].commentCounts").type(JsonFieldType.NUMBER).description("댓글 수"),
                    fieldWithPath("data[].likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                    fieldWithPath("data[].hateCount").type(JsonFieldType.NUMBER).description("싫어요 수"),
                    fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).description("생성 일시"),
                    fieldWithPath("data[].lastModifiedAt").type(JsonFieldType.STRING).description("수정 일시")
                )
            ));
    }

    @DisplayName("게시글 상세 조회 API 문서화")
    @Test
    void postDetailSearch() throws Exception {

        // given
        PostCreateServiceDto requestDto = new PostCreateServiceDto(1L, "게시글 제목입니다.",
            "게시글 본문입니다.", false);

        when(postQueryUseCase.findById(anyLong()))
            .thenReturn(PostDetailResponseDto.builder()
                .id(1L)
                .nickname("패캠러")
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .anonymity(requestDto.isAnonymity())
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build());

        // when then
        mockMvc.perform(get("/api/v1/post/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("post-search",
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
                    fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
                    fieldWithPath("data.content").type(JsonFieldType.STRING).description("게시글 본문"),
                    fieldWithPath("data.anonymity").type(JsonFieldType.BOOLEAN)
                        .description("익명 여부"),
                    fieldWithPath("data.likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                    fieldWithPath("data.hateCount").type(JsonFieldType.NUMBER).description("싫어요 수"),
                    fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 일시"),
                    fieldWithPath("data.lastModifiedAt").type(JsonFieldType.STRING).description("수정 일시")
                )
            ));
    }

    @DisplayName("게시글 수정 API 문서화")
    @Test
    void postUpdate() throws Exception {

        // given
        PostUpdateServiceDto requestDto = new PostUpdateServiceDto(1L, 1L, "새로운 게시글 제목입니다.",
            "새로운 게시글 본문입니다.");

        when(postCommandUseCase.updatePost(any(PostUpdateServiceDto.class)))
            .thenReturn(PostDetailResponseDto.builder()
                .id(1L)
                .nickname("패캠러")
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .anonymity(false)
                .likeCount(5)
                .hateCount(1)
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build());

        // when then
        mockMvc.perform(patch("/api/v1/post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andDo(document("post-update",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글 식별자"),
                    fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                    fieldWithPath("title").type(JsonFieldType.STRING).description("제목")
                        .attributes(new Attribute("constraints", "50자 이하")),
                    fieldWithPath("content").type(JsonFieldType.STRING).description("내용")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).optional()
                        .description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답데이터"),
                    fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("게시글 식별자"),
                    fieldWithPath("data.title").type(JsonFieldType.STRING).description("게시글 제목"),
                    fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
                    fieldWithPath("data.content").type(JsonFieldType.STRING).description("게시글 본문"),
                    fieldWithPath("data.anonymity").type(JsonFieldType.BOOLEAN)
                        .description("익명 여부"),
                    fieldWithPath("data.likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                    fieldWithPath("data.hateCount").type(JsonFieldType.NUMBER).description("싫어요 수"),
                    fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 일시"),
                    fieldWithPath("data.lastModifiedAt").type(JsonFieldType.STRING).description("수정 일시")
                )
            ));
    }

    @DisplayName("게시글 삭제 API 문서화")
    @Test
    void postDelete() throws Exception {

        // given
        PostDeleteRequestDto requestDto = new PostDeleteRequestDto(1L, 1L);

        doNothing().when(postCommandUseCase).deletePost(any(PostDeleteServiceDto.class));

        // when then
        mockMvc.perform(delete("/api/v1/post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andDo(document("post-delete",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글 식별자"),
                    fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 식별자")
                )
            ));
    }
}
