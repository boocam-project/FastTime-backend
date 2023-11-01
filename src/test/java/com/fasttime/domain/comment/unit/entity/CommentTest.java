package com.fasttime.domain.comment.unit.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasttime.domain.comment.entity.Comment;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CommentTest {

    @DisplayName("댓글을 생성할 수 있다.")
    @Test
    void create_comment_willSuccess() {
        // given
        String content = "test";
        boolean anonymity = true;

        // when
        Comment comment = Comment.builder().article(null).member(null).content(content)
            .anonymity(anonymity).parentComment(null).build();

        // then
        assertThat(comment).extracting("content", "anonymity", "parentComment")
            .containsExactly(content, anonymity, null);
    }

    @DisplayName("댓글을 수정할 수 있다.")
    @Test
    void update_content_willSuccess() {
        // given
        Comment comment = Comment.builder().article(null).member(null).content("test").anonymity(true)
            .parentComment(null).build();
        String content = "change";

        // when
        comment.updateContent(content);

        // then
        assertThat(comment).extracting("content", "anonymity", "parentComment")
            .containsExactly(content, true, null);
    }

    @DisplayName("댓글을 삭제할 수 있다.")
    @Test
    void delete_comment_willSuccess() {
        // given
        Comment comment = Comment.builder().article(null).member(null).content("test").anonymity(true)
            .parentComment(null).build();

        // when
        comment.delete(LocalDateTime.now());

        // then
        assertThat(comment.getDeletedAt()).isNotNull();
    }
}
