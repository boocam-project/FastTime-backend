package com.fasttime.domain.post.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.fasttime.domain.post.entity.Post;
import com.fasttime.domain.post.entity.ReportStatus;
import com.fasttime.domain.post.repository.PostRepository;
import com.fasttime.domain.post.service.PostQueryService;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
public class PostQueryServiceTest {

    @InjectMocks
    private PostQueryService postQueryService;

    @Mock
    private PostRepository postRepository;

    @DisplayName("searchById()는")
    @Nested
    class Context_searchById {

        @DisplayName("key를 넘기면 PostResponse를 반환한다.")
        @CsvSource(value = {
            "1, 제목1, 내용1, true, 10, 20",
            "2, 제목2, 내용2, false, 15, 30",
            "3, 제목3, 내용3, true, 23, 35"
        })
        @ParameterizedTest
        void inputKey_postResponse_willReturn(long id, String title, String content,
            boolean anonymity, int likeCount, int hateCount) {

            // given
            given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(Post.builder()
                    .id(id)
                    .title(title)
                    .content(content)
                    .anonymity(anonymity)
                    .likeCount(likeCount)
                    .hateCount(hateCount)
                    .reportStatus(ReportStatus.NORMAL)
                    .build()));

            // when
            Post postEntity = postQueryService.findById(id);

            // then
            assertThat(postEntity)
                .extracting("id", "title", "content", "anonymity", "likeCount", "hateCount",
                    "reportStatus")
                .containsExactly(id, title, content, anonymity, likeCount, hateCount,
                    ReportStatus.NORMAL);
        }

        @DisplayName("DB에 해당 id 를 가지는 게시글이 없다면 IllegalArgumentException을 던진다.")
        @Test
        void postDoesntExist_inDB_willThrowIllArgumentException() {

            // given
            given(postRepository.findById(anyLong())).willReturn(Optional.empty());

            // when then
            assertThatThrownBy(() -> postQueryService.findById(1L))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
