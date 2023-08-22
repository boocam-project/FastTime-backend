package com.fasttime.domain.post.unit.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasttime.domain.post.dto.service.request.PostCreateServiceDto;
import com.fasttime.domain.post.dto.service.response.PostResponseDto;
import com.fasttime.domain.post.entity.Post;
import com.fasttime.domain.post.repository.PostRepository;
import com.fasttime.domain.post.service.PostCommandService;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class PostCommandServiceTest {

    @Autowired
    private PostCommandService postCommandService;

    @Autowired
    private PostRepository postRepository;

    @DisplayName("writePost()는")
    @Nested
    class Context_writePost {

        @DisplayName("게시글을 DB에 성공적으로 저장한다.")
        @Test
        void _willSuccess() {
            // given
            PostCreateServiceDto dto = new PostCreateServiceDto(1L, "title", "content", true);

            // when
            PostResponseDto response = postCommandService.writePost(dto);

            // then
            Optional<Post> optionalPost = postRepository.findById(response.getId());
            assertThat(optionalPost).isPresent();

            Post savedPost = optionalPost.get();
            assertThat(response).extracting(
                    "id", "title", "content", "anonymity", "likeCount", "hateCount")
                .containsExactly(savedPost.getId(), savedPost.getTitle(), savedPost.getContent(),
                    savedPost.isAnonymity(), savedPost.getLikeCount(), savedPost.getHateCount());
        }
    }
}
