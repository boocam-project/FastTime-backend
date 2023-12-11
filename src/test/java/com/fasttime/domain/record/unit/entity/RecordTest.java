package com.fasttime.domain.record.unit.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasttime.domain.article.entity.Article;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.record.entity.Record;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RecordTest {

    @DisplayName("좋아요/싫어요 Entity 를 생성할 수 있다.")
    @Test
    void create_record_willSuccess() {
        // given
        Article article = Article.builder().id(0L).build();
        Member member = Member.builder().id(0L).build();

        // when
        Record record = Record.builder().id(0L).article(article).member(member).isLike(true).build();

        // then
        assertThat(record).extracting("id", "member", "article", "isLike")
            .containsExactly(0L, member, article, true);
    }
}
