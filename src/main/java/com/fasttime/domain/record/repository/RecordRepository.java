package com.fasttime.domain.record.repository;

import com.fasttime.domain.record.entity.Record;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<Record, Long> {

    Optional<Record> findByMemberIdAndArticleId(Long memberId, Long articleId);

    Optional<List<Record>> findAllByArticleId(long articleId);
}
