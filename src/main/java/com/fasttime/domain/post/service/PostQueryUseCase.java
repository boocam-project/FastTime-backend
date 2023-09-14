package com.fasttime.domain.post.service;

import com.fasttime.domain.post.entity.Post;
import java.util.List;

public interface PostQueryUseCase {

    Post findById(Long id);

    List<Post> findByPageForTitle(String title, int page);
}
