package com.fasttime.domain.article.controller;

import com.fasttime.domain.article.dto.controller.request.ArticleCreateRequest;
import com.fasttime.domain.article.dto.controller.request.ArticleUpdateRequestV2;
import com.fasttime.domain.article.dto.service.response.ArticleResponse;
import com.fasttime.domain.article.dto.service.response.ArticlesResponse;
import com.fasttime.domain.article.service.usecase.ArticleCommandUseCase;
import com.fasttime.domain.article.service.usecase.ArticleCommandUseCase.ArticleCreateServiceRequest;
import com.fasttime.domain.article.service.usecase.ArticleCommandUseCase.ArticleDeleteServiceRequest;
import com.fasttime.domain.article.service.usecase.ArticleCommandUseCase.ArticleUpdateServiceRequest;
import com.fasttime.domain.article.service.usecase.ArticleQueryUseCase;
import com.fasttime.domain.article.service.usecase.ArticleQueryUseCase.ArticlesSearchRequestServiceDto;
import com.fasttime.global.util.ResponseDTO;
import com.fasttime.global.util.SecurityUtil;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/v2/articles")
@RestController
public class ArticleControllerV2 {

    private final ArticleCommandUseCase articleCommandUseCase;
    private final ArticleQueryUseCase articleQueryUseCase;
    private final SecurityUtil securityUtil;

    public ArticleControllerV2(ArticleCommandUseCase articleCommandUseCase,
        ArticleQueryUseCase articleQueryUseCase, SecurityUtil securityUtil) {
        this.articleCommandUseCase = articleCommandUseCase;
        this.articleQueryUseCase = articleQueryUseCase;
        this.securityUtil = securityUtil;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<String>> createArticle(
        @RequestBody @Valid ArticleCreateRequest requestDto) {
        ArticleResponse response = articleCommandUseCase.write(
            new ArticleCreateServiceRequest(securityUtil.getCurrentMemberId(), requestDto.title(),
                requestDto.content(), requestDto.isAnonymity()));

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ResponseDTO.res(HttpStatus.CREATED, "성공!",
                "/api/v2/articles/%d".formatted(response.id())));
    }

    @PutMapping("/{articleId}")
    public ResponseEntity<ResponseDTO<ArticleResponse>> updateArticle(
        @PathVariable Long articleId,
        @RequestBody @Valid ArticleUpdateRequestV2 requestDto) {

        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.OK, articleCommandUseCase.update(
                new ArticleUpdateServiceRequest(
                    articleId,
                    securityUtil.getCurrentMemberId(),
                    requestDto.title(),
                    requestDto.isAnonymity(), requestDto.content()
                ))));
    }

    @DeleteMapping("/{articleId}")
    public ResponseEntity<ResponseDTO<Void>> deleteArticle(
        @PathVariable Long articleId) {

        articleCommandUseCase.delete(new ArticleDeleteServiceRequest(
            articleId,
            securityUtil.getCurrentMemberId(),
            LocalDateTime.now()
        ));
        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.OK, null, null));
    }

    @GetMapping("/{articleId}")
    public ResponseEntity<ResponseDTO<ArticleResponse>> getArticle(@PathVariable long articleId) {

        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.OK,
                articleQueryUseCase.queryById(articleId)));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<ArticlesResponse>>> getArticles(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String nickname,
        @RequestParam(defaultValue = "0") int likeCount,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(defaultValue = "0") int page) {

        List<ArticlesResponse> serviceResponse = articleQueryUseCase.search(
            ArticlesSearchRequestServiceDto.builder()
                .title(title)
                .nickname(nickname)
                .likeCount(likeCount)
                .pageSize(pageSize)
                .page(page)
                .build());

        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.OK, serviceResponse));
    }
}
