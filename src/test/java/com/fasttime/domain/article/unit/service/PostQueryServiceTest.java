package com.fasttime.domain.article.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.article.dto.service.response.ArticleResponse;
import com.fasttime.domain.article.entity.Article;
import com.fasttime.domain.article.entity.ReportStatus;
import com.fasttime.domain.article.exception.ArticleNotFoundException;
import com.fasttime.domain.article.repository.ArticleRepository;
import com.fasttime.domain.article.service.ArticleQueryService;
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
    private ArticleQueryService postQueryService;

    @Mock
    private ArticleRepository postRepository;

    @DisplayName("searchById()는")
    @Nested
    class Context_searchById {

        @DisplayName("key를 넘기면 PostResponse를 반환한다.")
        @CsvSource(value = {
            "1, 제목1, 내용1, 패캠러1, true, 10, 20",
            "2, 제목2, 내용2, 패캠러2, false, 15, 30",
            "3, 제목3, 내용3, 패캠러3, true, 23, 35"
        })
        @ParameterizedTest
        void inputKey_postResponse_willReturn(long id, String title, String content,
            String nickname, boolean anonymity, int likeCount, int hateCount) {

            // given
            given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(Article.builder()
                    .id(id)
                    .title(title)
                    .member(Member.builder().nickname(nickname).build())
                    .content(content)
                    .anonymity(anonymity)
                    .likeCount(likeCount)
                    .hateCount(hateCount)
                    .reportStatus(ReportStatus.NORMAL)
                    .build()));

            // when
            ArticleResponse response = postQueryService.queryById(id);

            // then
            assertThat(response)
                .extracting("id", "title", "content", "nickname", "anonymity", "likeCount", "hateCount")
                .containsExactly(id, title, content, nickname, anonymity, likeCount, hateCount);
        }

        @DisplayName("DB에 해당 id 를 가지는 게시글이 없다면 IllegalArgumentException을 던진다.")
        @Test
        void postDoesntExist_inDB_willThrowIllArgumentException() {

            // given
            given(postRepository.findById(anyLong())).willReturn(Optional.empty());

            // when then
            assertThatThrownBy(() -> postQueryService.queryById(1L))
                .isInstanceOf(ArticleNotFoundException.class);
        }
    }
}
