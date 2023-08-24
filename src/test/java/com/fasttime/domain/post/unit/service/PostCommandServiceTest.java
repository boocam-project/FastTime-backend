package com.fasttime.domain.post.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.post.dto.service.request.PostCreateServiceDto;
import com.fasttime.domain.post.dto.service.request.PostDeleteServiceDto;
import com.fasttime.domain.post.dto.service.request.PostUpdateServiceDto;
import com.fasttime.domain.post.dto.service.response.PostResponseDto;
import com.fasttime.domain.post.entity.Post;
import com.fasttime.domain.post.entity.ReportStatus;
import com.fasttime.domain.post.repository.PostRepository;
import com.fasttime.domain.post.service.PostCommandService;
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
class PostCommandServiceTest {

    @InjectMocks
    private PostCommandService postCommandService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PostRepository postRepository;

    @DisplayName("writePost()는")
    @Nested
    class Context_writePost {

        @DisplayName("게시글을 DB에 성공적으로 저장한다.")
        @Test
        void _willSuccess() {
            // given
            Post mockPost = createMockPost("title", "content");
            Member member = Member.builder().id(1L).build();
            PostCreateServiceDto dto = new PostCreateServiceDto(1L, "title",
                "content", true);

            given(memberRepository.findById(1L)).willReturn(Optional.of(member));
            given(postRepository.save(any(Post.class))).willReturn(mockPost);

            // when
            PostResponseDto response = postCommandService.writePost(dto);

            // then
            assertThat(response).extracting("id", "title", "content", "anonymity", "likeCount",
                    "hateCount")
                .containsExactly(1L, "title", "content", true, 0, 0);
        }

        @DisplayName("회원 정보가 DB에 없는 경우 IllegalArgumentException을 던진다.")
        @Test
        void member_notExist_throwIllArgumentException() {
            // given
            Post mockPost = createMockPost("title", "content");
            PostCreateServiceDto dto = new PostCreateServiceDto(1L, "title",
                "content", true);

            given(memberRepository.findById(anyLong())).willReturn(Optional.empty());
            given(postRepository.save(any(Post.class))).willReturn(mockPost);

            // when then
            assertThatThrownBy(() -> postCommandService.writePost(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("회원 정보가 없습니다.");
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
            Post mockPost = createMockPost(member, "title", "content");
            PostUpdateServiceDto serviceDto = new PostUpdateServiceDto(1L, 1L,
                "newContent");

            given(memberRepository.findById(1L)).willReturn(Optional.of(member));
            given(postRepository.findById(anyLong())).willReturn(Optional.of(mockPost));

            // when
            PostResponseDto response = postCommandService.updatePost(serviceDto);

            // then
            assertThat(response).extracting("id", "title", "content", "anonymity", "likeCount",
                    "hateCount")
                .containsExactly(1L, "title", "newContent", true, 0, 0);
        }

        @DisplayName("수정할 게시글 정보가 DB에 없는 경우 IllegalArgumentException을 던진다.")
        @Test
        void post_notExist_throwIllArgumentException() {
            // given
            PostUpdateServiceDto serviceDto = new PostUpdateServiceDto(1L, 1L,
                "newContent");

            given(postRepository.findById(anyLong())).willReturn(Optional.empty());

            // when then
            assertThatThrownBy(() -> postCommandService.updatePost(serviceDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 게시글입니다.");
        }

        @DisplayName("게시글 작성자가 아닌 경우 IllegalArgumentException을 던진다.")
        @Test
        void member_validateFail_throwIllArgumentException() {
            // given
            Member member = Member.builder().id(1L).build();
            Post mockPost = createMockPost(member, "title", "content");
            PostUpdateServiceDto serviceDto = new PostUpdateServiceDto(1L, 2L,
                "newContent");

            given(postRepository.findById(anyLong())).willReturn(Optional.of(mockPost));

            // when then
            assertThatThrownBy(() -> postCommandService.updatePost(serviceDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 게시글에 대한 권한이 없습니다.");
        }
    }

    @DisplayName("deletePost()는")
    @Nested
    class Context_deletePost {

        @DisplayName("수정할 게시글 정보가 DB에 없는 경우 IllegalArgumentException을 던진다.")
        @Test
        void post_notExist_throwIllArgumentException() {
            // given
            LocalDateTime deletedAt = LocalDateTime.now();
            PostDeleteServiceDto serviceDto = new PostDeleteServiceDto(1L, 1L, deletedAt);

            given(postRepository.findById(anyLong())).willReturn(Optional.empty());

            // when then
            assertThatThrownBy(() -> postCommandService.deletePost(serviceDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 게시글입니다.");
        }

        @DisplayName("게시글 작성자가 아닌 경우 IllegalArgumentException을 던진다.")
        @Test
        void member_validateFail_throwIllArgumentException() {
            // given
            LocalDateTime deletedAt = LocalDateTime.now();
            Member member = Member.builder().id(1L).build();
            PostDeleteServiceDto serviceDto = new PostDeleteServiceDto(1L, 100L, deletedAt);
            Post mockPost = createMockPost(member, "title", "content");
            given(postRepository.findById(anyLong())).willReturn(Optional.of(mockPost));

            // when then
            assertThatThrownBy(() -> postCommandService.deletePost(serviceDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 게시글에 대한 권한이 없습니다.");
        }
    }

    private Post createMockPost(String title, String content) {
        return Post.builder()
            .id(1L)
            .title(title)
            .content(content)
            .anonymity(true)
            .likeCount(0)
            .hateCount(0)
            .reportStatus(ReportStatus.NORMAL)
            .build();
    }

    private Post createMockPost(Member member, String title, String content) {
        return Post.builder()
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
