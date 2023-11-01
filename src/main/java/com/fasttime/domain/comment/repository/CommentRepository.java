package com.fasttime.domain.comment.repository;

import com.fasttime.domain.comment.entity.Comment;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.article.entity.Article;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<List<Comment>> findAllByArticle(Article article);

    Optional<List<Comment>> findAllByMember(Member member);
}
