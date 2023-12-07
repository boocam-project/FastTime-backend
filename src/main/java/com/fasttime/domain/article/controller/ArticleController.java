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
import com.fasttime.domain.article.service.usecase.ArticleQueryUseCase.ArticlesSearchServiceRequest;
import com.fasttime.global.util.ResponseDTO;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1/article")
@RestController
public class ArticleController {

    private static final String SESSION_MEMBER_KEY = "MEMBER";

    private final ArticleCommandUseCase articleCommandUseCase;
    private final ArticleQueryUseCase articleQueryUseCase;

    public ArticleController(ArticleCommandUseCase articleCommandUseCase,
        ArticleQueryUseCase articleQueryUseCase) {
        this.articleCommandUseCase = articleCommandUseCase;
        this.articleQueryUseCase = articleQueryUseCase;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<ArticleResponse>> createArticle(HttpSession session,
        @RequestBody @Valid ArticleCreateRequest requestDto) {

        Long memberId = (Long) session.getAttribute(SESSION_MEMBER_KEY);

        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ResponseDTO.res(HttpStatus.CREATED, articleCommandUseCase.write(
                new ArticleCreateServiceRequest(memberId,
                    requestDto.getTitle(),
                    requestDto.getContent(),
                    requestDto.isAnonymity()))));
    }

    @PutMapping
    public ResponseEntity<ResponseDTO<ArticleResponse>> updateArticle(HttpSession session,
        @RequestBody @Valid ArticleUpdateRequest requestDto) {

        Long memberId = (Long) session.getAttribute(SESSION_MEMBER_KEY);

        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.OK, articleCommandUseCase.update(
                new ArticleUpdateServiceRequest(
                    memberId,
                    requestDto.getMemberId(),
                    requestDto.getTitle(),
                    requestDto.isAnonymity(), requestDto.getContent()
                ))));
    }

    @DeleteMapping
    public ResponseEntity<ResponseDTO<Void>> deleteArticle(HttpSession session,
        @RequestBody @Valid ArticleDeleteRequest requestDto) {

        Long memberId = (Long) session.getAttribute(SESSION_MEMBER_KEY);

        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        articleCommandUseCase.delete(new ArticleDeleteServiceRequest(
            requestDto.getArticleId(),
            memberId,
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
            ArticlesSearchServiceRequest.builder()
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
