package com.fasttime.domain.article.service;

import com.fasttime.domain.article.dto.service.response.ArticleResponse;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;

public interface ArticleCommandUseCase {

    ArticleResponse write(ArticleCreateServiceRequest request);

    ArticleResponse update(ArticleUpdateServiceRequest request);

    void delete(ArticleDeleteServiceRequest request);

    void likeOrHate(ArticleLikeOrHateServiceRequest request);

    @Getter
    class ArticleCreateServiceRequest {

        @NotNull
        private final Long memberId;

        @NotBlank
        private final String title;

        @NotBlank
        private final String content;

        private final boolean anonymity;

        public ArticleCreateServiceRequest(Long memberId, String title, String content, boolean anonymity) {
            this.memberId = memberId;
            this.title = title;
            this.content = content;
            this.anonymity = anonymity;
        }
    }

    @Getter
    class ArticleUpdateServiceRequest {

        private final Long articleId;
        private final Long memberId;
        private final String title;
        private final String content;

        public ArticleUpdateServiceRequest(Long articleId, Long memberId, String title, String content) {
            this.articleId = articleId;
            this.memberId = memberId;
            this.title = title;
            this.content = content;
        }
    }

    @Getter
    class ArticleDeleteServiceRequest {

        private final Long articleId;
        private final Long memberId;
        private final LocalDateTime deletedAt;

        public ArticleDeleteServiceRequest(Long articleId, Long memberId, LocalDateTime deletedAt) {
            this.articleId = articleId;
            this.memberId = memberId;
            this.deletedAt = deletedAt;
        }
    }

    @Getter
    class ArticleLikeOrHateServiceRequest {

        private Long articleId;
        private boolean isLike;
        private boolean increase;

        public ArticleLikeOrHateServiceRequest(Long articleId, boolean isLike, boolean increase) {
            this.articleId = articleId;
            this.isLike = isLike;
            this.increase = increase;
        }
    }

}
