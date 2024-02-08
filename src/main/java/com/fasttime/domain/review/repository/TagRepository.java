package com.fasttime.domain.review.repository;

import com.fasttime.domain.review.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
public interface TagRepository extends JpaRepository<Tag, Long> {

}
