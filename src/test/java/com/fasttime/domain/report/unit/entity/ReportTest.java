package com.fasttime.domain.report.unit.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasttime.domain.article.entity.Article;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.report.entity.Report;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ReportTest {

    @DisplayName("신고 Entity 를 생성할 수 있다.")
    @Test
    void create_report_willSuccess() {
        // given
        Article post = Article.builder().id(0L).build();
        Member member = Member.builder().id(0L).build();

        // when
        Report report = Report.builder().id(0L).post(post).member(member).build();

        // then
        assertThat(report).extracting("id", "member", "post").containsExactly(0L, member, post);
    }
}
