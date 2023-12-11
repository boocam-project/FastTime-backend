package com.fasttime.domain.article.controller;

import com.fasttime.domain.article.dto.controller.request.ArticleCreateRequest;
import com.fasttime.domain.article.dto.controller.request.ArticleDeleteRequest;
import com.fasttime.domain.article.dto.controller.request.ArticleUpdateRequest;
import com.fasttime.domain.article.dto.service.response.ArticleResponse;
import com.fasttime.domain.article.dto.service.response.ArticlesResponse;
import com.fasttime.domain.article.service.usecase.ArticleCommandUseCase;
import com.fasttime.domain.article.service.usecase.ArticleCommandUseCase.ArticleCreateServiceRequest;
import com.fasttime.domain.article.service.usecase.ArticleCommandUseCase.ArticleDeleteServiceRequest;
import com.fasttime.domain.article.service.usecase.ArticleCommandUseCase.ArticleUpdateServiceRequest;
import com.fasttime.domain.article.service.usecase.ArticleQueryUseCase;
import com.fasttime.domain.article.service.usecase.ArticleQueryUseCase.ArticlesSearchRequest;
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

/**
 * @deprecated 2023.12.05
 * spring security를 사용하면서 더 이상 memberId 값을 받아올 필요가 없어졌습니다.
 * 해당 이유로 다음 api를 deprecated 하고 v2로 제공합니다.
 */
@Slf4j
@Deprecated(since = "2023.12.05")
@RequestMapping("/api/v1/article")
@RestController
public class ArticleController {

    private final ArticleCommandUseCase articleCommandUseCase;
    private final ArticleQueryUseCase articleQueryUseCase;
    private final SecurityUtil securityUtil;

    public ArticleController(ArticleCommandUseCase articleCommandUseCase,
        ArticleQueryUseCase articleQueryUseCase, SecurityUtil securityUtil) {
        this.articleCommandUseCase = articleCommandUseCase;
        this.articleQueryUseCase = articleQueryUseCase;
        this.securityUtil = securityUtil;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<ArticleResponse>> createArticle(
        @RequestBody @Valid ArticleCreateRequest requestDto) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ResponseDTO.res(HttpStatus.CREATED, articleCommandUseCase.write(
                new ArticleCreateServiceRequest(securityUtil.getCurrentMemberId(),
                    requestDto.title(),
                    requestDto.content(),
                    requestDto.isAnonymity()))));
    }

    @PutMapping
    public ResponseEntity<ResponseDTO<ArticleResponse>> updateArticle(
        @RequestBody @Valid ArticleUpdateRequest requestDto) {

        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.OK, articleCommandUseCase.update(
                new ArticleUpdateServiceRequest(
                    requestDto.articleId(),
                    requestDto.memberId(),
                    requestDto.title(),
                    requestDto.isAnonymity(), requestDto.content()
                ))));
    }

    @DeleteMapping
    public ResponseEntity<ResponseDTO<Void>> deleteArticle(
        @RequestBody @Valid ArticleDeleteRequest requestDto) {

        articleCommandUseCase.delete(new ArticleDeleteServiceRequest(
            requestDto.articleId(),
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
            ArticlesSearchRequest.builder()
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
