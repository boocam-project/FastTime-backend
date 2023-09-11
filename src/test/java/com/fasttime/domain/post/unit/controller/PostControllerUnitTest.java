package com.fasttime.domain.post.unit.controller;

import com.fasttime.utils.ControllerUnitTestContainer;

class PostControllerUnitTest extends ControllerUnitTestContainer {

//    @MockBean
//    private PostCommandService postCommandService;
//
//    @WithMockUser
//    @DisplayName("입력 양식을 정상적으로 입력받으면 정상적으로 프로세스가 진행된다.")
//    @CsvSource({"1, title1, This is Content1, true", "2, title2, This is Content2, false"})
//    @ParameterizedTest
//    void allProperty_isOk_willPass(Long id, String title, String content, boolean isAnonymity)
//        throws Exception {
//        // given
//        PostCreateRequestDto postCreateRequestDto = new PostCreateRequestDto(id, title, content,
//            isAnonymity);
//
//        when(postCommandService.writePost(any(PostCreateServiceDto.class)))
//            .thenReturn(PostDetailResponseDto.builder()
//                .id(id)
//                .title(title)
//                .content(content)
//                .anonymity(isAnonymity)
//                .likeCount(0)
//                .hateCount(0)
//                .build());
//
//        // when then
//        mockMvc.perform(post("/api/v1/post")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(postCreateRequestDto)))
//            .andDo(print())
//            .andExpect(status().isCreated())
//            .andExpect(jsonPath("$.id").value("201"))
//            .andExpect(jsonPath("$.title").value(title))
//            .andExpect(jsonPath("$.content").value(content))
//            .andExpect(jsonPath("$.anonymity").value(isAnonymity))
//            .andExpect(jsonPath("$.likeCount").value(0))
//            .andExpect(jsonPath("$.hateCount").value(0));
//    }
//
//    @DisplayName("writePost()는")
//    @Nested
//    class Context_writePost {
//
//    }


}
