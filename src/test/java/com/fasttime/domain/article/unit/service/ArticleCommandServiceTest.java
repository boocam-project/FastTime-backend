package com.fasttime.domain.article.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

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
import com.fasttime.domain.article.service.ArticleCommandUseCase.ArticleCreateServiceRequest;
import com.fasttime.domain.article.service.ArticleCommandUseCase.ArticleDeleteServiceRequest;
import com.fasttime.domain.article.service.ArticleCommandUseCase.ArticleUpdateServiceRequest;
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
    private ArticleRepository postRepository;

    @DisplayName("writePost()는")
    @Nested
    class Context_writePost {

        @DisplayName("게시글을 DB에 성공적으로 저장한다.")
        @Test
        void _willSuccess() {
            // given
            Member member = Member.builder().id(1L).nickname("패캠러").build();
            Article mockPost = createMockPost("title", "content", member);
            ArticleCreateServiceRequest dto = new ArticleCreateServiceRequest(1L, "title",
                "content", true);

            given(memberService.getMember(1L)).willReturn(member);
            given(postRepository.save(any(Article.class))).willReturn(mockPost);

            // when
            ArticleResponse response = postCommandService.write(dto);

            // then
            assertThat(response).extracting("id", "title", "content", "nickname", "anonymity", "likeCount", "hateCount")
                .containsExactly(1L, "title", "content", "패캠러", true, 0, 0);
        }

        @DisplayName("회원 정보가 DB에 없는 경우 UserNotFoundException을 던진다.")
        @Test
        void member_notExist_throwIllArgumentException() {
            // given
            Article mockPost = createMockPost("title", "content", null);
            ArticleCreateServiceRequest dto = new ArticleCreateServiceRequest(1L, "title",
                "content", true);

            given(memberService.getMember(1L)).willThrow(UserNotFoundException.class);
            given(postRepository.save(any(Article.class))).willReturn(mockPost);

            // when then
            assertThatThrownBy(() -> postCommandService.write(dto))
                .isInstanceOf(UserNotFoundException.class);
        }
    }

    @DisplayName("updatePost()는")
    @Nested
    class Context_updatePost {

        @DisplayName("게시글을 DB에 성공적으로 업데이트한다.")
        @Test
        void _willSuccess() {
            // given
            Member member = Member.builder().id(1L).build();
            Article mockPost = createMockPost(member, "title", "content");
            ArticleUpdateServiceRequest serviceDto = new ArticleUpdateServiceRequest(1L, 1L,
                "new title", "newContent");

            given(memberService.getMember(1L)).willReturn(member);
            given(postRepository.findById(anyLong())).willReturn(Optional.of(mockPost));

            // when
            ArticleResponse response = postCommandService.update(serviceDto);

            // then
            assertThat(response).extracting("id", "title", "content", "anonymity", "likeCount",
                    "hateCount")
                .containsExactly(1L, "new title", "newContent", true, 0, 0);
        }

        @DisplayName("수정할 게시글 정보가 DB에 없는 경우 PostNotFoundException을 던진다.")
        @Test
        void post_notExist_throwIllArgumentException() {
            // given
            ArticleUpdateServiceRequest serviceDto = new ArticleUpdateServiceRequest(1L, 1L,
                "newTitle", "newContent");

            given(postRepository.findById(anyLong())).willReturn(Optional.empty());

            // when then
            assertThatThrownBy(() -> postCommandService.update(serviceDto))
                .isInstanceOf(ArticleNotFoundException.class);
        }

        @DisplayName("게시글 작성자가 아닌 경우 NotPostWriterException을 던진다.")
        @Test
        void member_validateFail_throwIllArgumentException() {
            // given
            Member writer = Member.builder().id(1L).build();
            Member notAuthorizedMember = Member.builder().id(100L).build();
            Article mockPost = createMockPost(writer, "title", "content");
            ArticleUpdateServiceRequest serviceDto = new ArticleUpdateServiceRequest(1L, notAuthorizedMember.getId(),
                "newTitle", "newContent");

            given(postRepository.findById(anyLong())).willReturn(Optional.of(mockPost));
            given(memberService.getMember(anyLong())).willReturn(notAuthorizedMember);

            // when then
            assertThatThrownBy(() -> postCommandService.update(serviceDto))
                .isInstanceOf(NotArticleWriterException.class);
        }
    }

    @DisplayName("deletePost()는")
    @Nested
    class Context_deletePost {

        @DisplayName("수정할 게시글 정보가 DB에 없는 경우 PostNotFoundException을 던진다.")
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
            Article mockPost = createMockPost(writer, "title", "content");
            given(postRepository.findById(anyLong())).willReturn(Optional.of(mockPost));
            given(memberService.getMember(anyLong())).willReturn(notAuthorizedMember);

            // when then
            assertThatThrownBy(() -> postCommandService.delete(serviceDto))
                .isInstanceOf(NotArticleWriterException.class);
        }
    }

    private Article createMockPost(String title, String content, Member member) {
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

    private Article createMockPost(Member member, String title, String content) {
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
