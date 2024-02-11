package com.fasttime.domain.article.service;

import com.fasttime.domain.article.entity.Article;
import com.fasttime.domain.article.exception.ArticleNotFoundException;
import com.fasttime.domain.article.repository.ArticleRepository;
import com.fasttime.domain.comment.infra.CommentCreateEvent;
import com.fasttime.domain.comment.infra.CommentDeleteEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Component
public class ArticleEventListener {

    private final ArticleRepository articleRepository;

    public ArticleEventListener(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @TransactionalEventListener
    public void updateCommentCount(CommentCreateEvent event) {
        Article article = articleRepository.findById(event.articleId())
            .orElseThrow(() -> new ArticleNotFoundException(event.articleId()));

        article.increaseCommentCount();
    }

    @TransactionalEventListener
    public void updateCommentCount(CommentDeleteEvent event) {
        Article article = articleRepository.findById(event.articleId())
            .orElseThrow(() -> new ArticleNotFoundException(event.articleId()));

        article.decreaseCommentCount();
    }
}
