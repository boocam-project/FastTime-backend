package com.fasttime.domain.article.unit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasttime.domain.article.entity.Article;
import com.fasttime.domain.article.repository.ArticleRepository;
import com.fasttime.domain.article.repository.ArticleRepositoryCustom.ArticleQueryResponse;
import com.fasttime.domain.article.service.ArticleQueryUseCase.ArticleSearchCondition;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.repository.MemberRepository;
import java.util.List;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ArticleRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ArticleRepository postRepository;

    @Nested
    class Context_queryArticles {

        @BeforeEach
        public void tearDown() {
            postRepository.deleteAll();
        }

        @DisplayName("검색 제목을 포함하는 게시글들을 찾아낼 수 있다.")
        @ValueSource(strings = {"제목", "목입", "제목입니다.", "니다."})
        @ParameterizedTest
        void postsContainsTitle_willReturn(String searchTarget) {

            // given
            Member savedMember = memberRepository.save(
                Member.builder().id(1L).nickname("nickname1").build());

            postRepository.save(
                Article.createNewArticle(savedMember, "제목입니다.", "content1", false));

            // when
            List<ArticleQueryResponse> result = postRepository
                .search(ArticleSearchCondition.builder()
                    .title(searchTarget)
                    .pageSize(10)
                    .build());

            // then
            assertThat(result)
                .hasSize(1)
                .extracting("title", "nickname")
                .contains(Tuple.tuple("제목입니다.", "nickname1"));
        }

        @DisplayName("검색 닉네임을 포함하는 게시글들을 찾아낼 수 있다.")
        @ValueSource(strings = {"nickname", "nickname1", "nick", "ckname"})
        @ParameterizedTest
        void postsContainsNickname_willReturn(String searchTarget) {

            // given
            Member savedMember = memberRepository.save(
                Member.builder().id(1L).nickname("nickname1").build());

            postRepository.save(
                Article.createNewArticle(savedMember, "title1", "content1", false));

            // when
            List<ArticleQueryResponse> result = postRepository
                .search(ArticleSearchCondition.builder()
                    .nickname(searchTarget)
                    .pageSize(10)
                    .build());

            // then
            assertThat(result)
                .hasSize(1)
                .extracting("title", "nickname")
                .contains(Tuple.tuple("title1", "nickname1"));
        }

        @DisplayName("좋아요 수보다 큰 게시물들을 찾을 수 있다.")
        @ValueSource(ints = {1, 2, 100})
        @ParameterizedTest
        void postsContainsGreaterThanLike_willReturn(int searchTarget) {

            // given
            Member savedMember = memberRepository.save(
                Member.builder().id(1L).nickname("nickname1").build());

            postRepository.save(
                Article.createNewArticle(savedMember, "title1", "content1", false));

            // when
            List<ArticleQueryResponse> result = postRepository
                .search(ArticleSearchCondition.builder()
                    .likeCount(searchTarget)
                    .pageSize(10)
                    .build());

            // then
            assertThat(result).isEmpty();
        }
    }
}
