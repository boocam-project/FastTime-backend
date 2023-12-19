package com.fasttime.domain.article.repository;

import com.fasttime.domain.article.service.usecase.ArticleQueryUseCase.ArticlesSearchRequestServiceDto;
import java.util.List;

public interface ArticleRepositoryCustom {

    List<ArticleQueryResponse> search(ArticlesSearchRequestServiceDto searchCondition);
}
