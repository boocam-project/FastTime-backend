package com.fasttime.domain.article.repository;

import com.fasttime.domain.article.entity.Article;
import com.fasttime.domain.article.entity.ReportStatus;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long>, ArticleRepositoryCustom {

    List<Article> findAllByReportStatus(Pageable pageable, ReportStatus reportStatus);
}
