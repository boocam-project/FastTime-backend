package com.fasttime.domain.article.service;

import com.fasttime.domain.article.dto.service.response.ArticleResponse;
import com.fasttime.domain.article.dto.service.response.ArticlesResponse;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

public interface ArticleQueryUseCase {

    ArticleResponse queryById(Long id);

    List<ArticlesResponse> search(ArticleSearchCondition articleSearchCondition);

    @Getter
    class ArticleSearchCondition {

        private final String nickname;
        private final String title;
        private final int likeCount;
        private final int pageSize;
        private final int page;

        @Builder
        private ArticleSearchCondition(String nickname, String title, int likeCount, int pageSize,
            int page) {
            this.nickname = nickname;
            this.title = title;
            this.likeCount = likeCount;
            this.pageSize = pageSize;
            this.page = page;
        }
    }
}
