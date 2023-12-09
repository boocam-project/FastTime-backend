package com.fasttime.domain.article.unit.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasttime.domain.article.dto.controller.request.ArticleCreateRequest;
import com.fasttime.util.ControllerUnitTestSupporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;

class ArticleControllerTest extends ControllerUnitTestSupporter {

    @DisplayName("writePost(PostCreateRequestDto)는")
    @Nested
    class Context_WritePost {

        @DisplayName("게시글을 저장할 수 있다.")
        @Test
        void article_willSave() throws Exception {

            // given
            ArticleCreateRequest requestDto = new ArticleCreateRequest("title",
                "this is content", false);

            MockHttpSession session = new MockHttpSession();
            session.setAttribute("MEMBER", 1L);

            // when then
            mockMvc.perform(post("/api/v1/article")
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session)
                    .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated());
        }

        @DisplayName("회원 ID가 없는 경우 회원 관련 에러가 발생한다.")
        @Test
        void whenMemberId_isNull_willSend400Error() throws Exception {

            // given
            ArticleCreateRequest requestDto = new ArticleCreateRequest("title",
                "this is content", false);

            // when then
            mockMvc.perform(post("/api/v1/article")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isForbidden());
        }
    }
}
