package com.fasttime.domain.post.unit.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasttime.domain.post.entity.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PostTest {

    @DisplayName("게시글을 생성할 수 있다.")
    @Test
    void post_create_willSuccess(){
        // given
        String title = "제목1";
        String content = "내용1";
        boolean anounumity = true;

        // when
        Post createdPost = Post.createNewPost(null, title, content, anounumity);

        // then
        assertThat(createdPost).extracting("title", "content", "anonymity")
            .containsExactly(title, content, anounumity);
    }

    @DisplayName("게시글의 내용을 변경할 수 있다.")
    @Test
    void post_update_willSuccess(){
        // given
        String title = "제목1";
        String content = "내용1";
        boolean anounumity = true;
        Post createdPost = Post.createNewPost(null, title, content, anounumity);

        // when
        String updateContent = "새로운 내용1";
        createdPost.update(updateContent);

        // then
        assertThat(createdPost).extracting("title", "content", "anonymity")
            .containsExactly(title, updateContent, anounumity);
    }
}
