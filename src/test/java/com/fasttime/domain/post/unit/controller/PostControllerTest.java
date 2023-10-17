package com.fasttime.domain.post.unit.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasttime.domain.post.service.PostCommandUseCase.PostCreateServiceDto;
import com.fasttime.util.ControllerUnitTestSupporter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

@Disabled
class PostControllerTest extends ControllerUnitTestSupporter {

    @DisplayName("writePost(PostCreateRequestDto)는")
    @Nested
    class Context_WritePost {

        @DisplayName("게시글을 저장할 수 있다.")
        @Test
        void post_willSave() throws Exception {

            // given
            PostCreateServiceDto requestDto = new PostCreateServiceDto(1L, "title",
                "this is content", false);

            // when then
            mockMvc.perform(post("/api/v1/post")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated());
        }

        @DisplayName("회원 ID가 없는 경우 회원 관련 에러가 발생한다.")
        @Test
        void whenMemberId_isNull_willSend400Error() throws Exception {

            // given
            PostCreateServiceDto requestDto = new PostCreateServiceDto(null, "title",
                "this is content", false);

            // when then
            mockMvc.perform(post("/api/v1/post")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
        }
    }
}
