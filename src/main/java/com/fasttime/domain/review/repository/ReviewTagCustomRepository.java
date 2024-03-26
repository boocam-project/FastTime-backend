package com.fasttime.domain.review.repository;

import java.util.List;

public interface ReviewTagCustomRepository {

    List<Object[]> countTagsByBootcampGroupedByTagId(String bootcampName);
}
