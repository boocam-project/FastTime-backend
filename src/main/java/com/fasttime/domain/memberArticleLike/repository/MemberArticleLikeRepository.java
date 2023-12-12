package com.fasttime.domain.memberArticleLike.repository;

import com.fasttime.domain.memberArticleLike.entity.MemberArticleLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberArticleLikeRepository extends JpaRepository<MemberArticleLike, Long> {

    Optional<MemberArticleLike> findByMemberIdAndArticleId(Long memberId, Long articleId);
}
