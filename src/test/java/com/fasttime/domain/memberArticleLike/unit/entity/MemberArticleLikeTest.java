package com.fasttime.domain.memberArticleLike.unit.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasttime.domain.article.entity.Article;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.memberArticleLike.entity.MemberArticleLike;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MemberArticleLikeTest {

    @DisplayName("좋아요/싫어요 Entity 를 생성할 수 있다.")
    @Test
    void create_record_willSuccess() {
        // given
        Article article = Article.builder().id(0L).build();
        Member member = Member.builder().id(0L).build();

        // when
        MemberArticleLike memberArticleLike = MemberArticleLike.builder().id(0L).article(article)
            .member(member).isLike(true).build();

        // then
        assertThat(memberArticleLike).extracting("id", "member", "article", "isLike")
            .containsExactly(0L, member, article, true);
    }
}
