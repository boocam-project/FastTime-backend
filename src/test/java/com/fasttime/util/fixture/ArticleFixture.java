package com.fasttime.util.fixture;

import com.fasttime.domain.article.entity.Article;
import com.fasttime.domain.article.entity.ReportStatus;
import com.fasttime.domain.member.entity.Member;
import java.util.ArrayList;
import java.util.List;

public class ArticleFixture {

    private ArticleFixture() {}

    public static Article createNewArticle(Member member) {
        return Article.createNewArticle(member, "제목입니다.", "content1", false);
    }

    public static Article createArticle(Member member) {
        return Article.builder()
            .id(1L)
            .title("제목1")
            .content("내용1")
            .member(member)
            .likeCount(10)
            .hateCount(2)
            .anonymity(true)
            .reportStatus(ReportStatus.NORMAL)
            .build();
    }

    public static List<Article> create10Articles(Member member) {
        List<Article> articles = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            Article article = Article.builder()
                .title("제목%d".formatted(i))
                .content("내용%d".formatted(i))
                .member(member)
                .likeCount(i)
                .hateCount(i)
                .anonymity(true)
                .reportStatus(ReportStatus.NORMAL)
                .build();

            articles.add(article);
        }

        return articles;
    }

}
