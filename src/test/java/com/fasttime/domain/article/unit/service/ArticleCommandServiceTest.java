package com.fasttime.domain.article.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.fasttime.domain.article.service.ArticleSettingProvider;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.exception.UserNotFoundException;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.domain.article.dto.service.response.ArticleResponse;
import com.fasttime.domain.article.entity.Article;
import com.fasttime.domain.article.entity.ReportStatus;
import com.fasttime.domain.article.exception.NotArticleWriterException;
import com.fasttime.domain.article.exception.ArticleNotFoundException;
import com.fasttime.domain.article.repository.ArticleRepository;
import com.fasttime.domain.article.service.ArticleCommandService;
import com.fasttime.domain.article.service.usecase.ArticleCommandUseCase.ArticleCreateServiceRequest;
import com.fasttime.domain.article.service.usecase.ArticleCommandUseCase.ArticleDeleteServiceRequest;
import com.fasttime.domain.article.service.usecase.ArticleCommandUseCase.ArticleUpdateServiceRequest;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class ArticleCommandServiceTest {

    @InjectMocks
    private ArticleCommandService postCommandService;

    @Mock
    private MemberService memberService;

    @Mock
    private ArticleSettingProvider articleSettingProvider;

    @Mock
    private ArticleRepository postRepository;

    @DisplayName("createArticle()는")
    @Nested
    class Context_createArticle {

        @DisplayName("게시글을 DB에 성공적으로 저장한다.")
        @Test
        void _willSuccess() {
            // given
            Member member = Member.builder().id(1L).nickname("패캠러").build();
            Article mockArticle = createMockArticle("title", "content", member);
            ArticleCreateServiceRequest dto = new ArticleCreateServiceRequest(1L, "title",
                "content", true);

            given(memberService.getMember(1L)).willReturn(member);
            given(postRepository.save(any(Article.class))).willReturn(mockArticle);

            // when
            ArticleResponse response = postCommandService.write(dto);

            // then
            assertThat(response).extracting("id", "title", "content", "nickname", "anonymity", "likeCount", "hateCount")
                .containsExactly(1L, "title", "content", articleSettingProvider.getAnonymousNickname(), true, 0, 0);
        }

        @DisplayName("회원 정보가 DB에 없는 경우 UserNotFoundException을 던진다.")
        @Test
        void member_notExist_throwIllArgumentException() {
            // given
            Article mockArticle = createMockArticle("title", "content", null);
            ArticleCreateServiceRequest dto = new ArticleCreateServiceRequest(1L, "title",
                "content", true);

            given(memberService.getMember(1L)).willThrow(UserNotFoundException.class);
            given(postRepository.save(any(Article.class))).willReturn(mockArticle);

            // when then
            assertThatThrownBy(() -> postCommandService.write(dto))
                .isInstanceOf(UserNotFoundException.class);
        }
    }

    @DisplayName("updateArticle()는")
    @Nested
    class Context_updateArticle {

        @DisplayName("게시글을 DB에 성공적으로 업데이트한다.")
        @Test
        void _willSuccess() {
            // given
            Member member = Member.builder().id(1L).build();
            Article mockArticle = createMockArticle(member, "title", "content");
            ArticleUpdateServiceRequest serviceDto = new ArticleUpdateServiceRequest(1L, 1L,
                "new title", true, "newContent");

            given(memberService.getMember(1L)).willReturn(member);
            given(postRepository.findById(anyLong())).willReturn(Optional.of(mockArticle));

            // when
            ArticleResponse response = postCommandService.update(serviceDto);

            // then
            assertThat(response).extracting("id", "title", "content", "anonymity", "likeCount",
                    "hateCount")
                .containsExactly(1L, "new title", "newContent", true, 0, 0);
        }

        @DisplayName("수정할 게시글 정보가 DB에 없는 경우 ArticleNotFoundException을 던진다.")
        @Test
        void post_notExist_throwIllArgumentException() {
            // given
            ArticleUpdateServiceRequest serviceDto = new ArticleUpdateServiceRequest(1L, 1L,
                "newTitle", true, "newContent");

            given(postRepository.findById(anyLong())).willReturn(Optional.empty());

            // when then
            assertThatThrownBy(() -> postCommandService.update(serviceDto))
                .isInstanceOf(ArticleNotFoundException.class);
        }

        @DisplayName("게시글 작성자가 아닌 경우 NotArticleWriterException을 던진다.")
        @Test
        void member_validateFail_throwIllArgumentException() {
            // given
            Member writer = Member.builder().id(1L).build();
            Member notAuthorizedMember = Member.builder().id(100L).build();
            Article mockArticle = createMockArticle(writer, "title", "content");
            ArticleUpdateServiceRequest serviceDto = new ArticleUpdateServiceRequest(1L, notAuthorizedMember.getId(),
                "newTitle", true, "newContent");

            given(postRepository.findById(anyLong())).willReturn(Optional.of(mockArticle));
            given(memberService.getMember(anyLong())).willReturn(notAuthorizedMember);

            // when then
            assertThatThrownBy(() -> postCommandService.update(serviceDto))
                .isInstanceOf(NotArticleWriterException.class);
        }
    }

    @DisplayName("deleteArticle()는")
    @Nested
    class Context_deleteArticle {

        @DisplayName("수정할 게시글 정보가 DB에 없는 경우 ArticleNotFoundException을 던진다.")
        @Test
        void post_notExist_throwIllArgumentException() {
            // given
            LocalDateTime deletedAt = LocalDateTime.now();
            ArticleDeleteServiceRequest serviceDto = new ArticleDeleteServiceRequest(1L, 1L, deletedAt);

            given(postRepository.findById(anyLong())).willReturn(Optional.empty());

            // when then
            assertThatThrownBy(() -> postCommandService.delete(serviceDto))
                .isInstanceOf(ArticleNotFoundException.class);
        }

        @DisplayName("게시글 작성자가 아닌 경우 UserNotFoundException을 던진다.")
        @Test
        void member_validateFail_throwIllArgumentException() {
            // given
            LocalDateTime deletedAt = LocalDateTime.now();
            Member writer = Member.builder().id(1L).build();
            Member notAuthorizedMember = Member.builder().id(100L).build();

            ArticleDeleteServiceRequest serviceDto = new ArticleDeleteServiceRequest(1L, notAuthorizedMember.getId(), deletedAt);
            Article mockArticle = createMockArticle(writer, "title", "content");
            given(postRepository.findById(anyLong())).willReturn(Optional.of(mockArticle));
            given(memberService.getMember(anyLong())).willReturn(notAuthorizedMember);

            // when then
            assertThatThrownBy(() -> postCommandService.delete(serviceDto))
                .isInstanceOf(NotArticleWriterException.class);
        }
    }

    private Article createMockArticle(String title, String content, Member member) {
        return Article.builder()
            .id(1L)
            .title(title)
            .member(member)
            .content(content)
            .anonymity(true)
            .likeCount(0)
            .hateCount(0)
            .reportStatus(ReportStatus.NORMAL)
            .build();
    }

    private Article createMockArticle(Member member, String title, String content) {
        return Article.builder()
            .id(1L)
            .member(member)
            .title(title)
            .content(content)
            .anonymity(true)
            .likeCount(0)
            .hateCount(0)
            .reportStatus(ReportStatus.NORMAL)
            .build();
    }
}
