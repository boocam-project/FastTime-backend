package com.fasttime.domain.post.service;

import com.fasttime.domain.post.entity.Post;
import com.fasttime.domain.post.repository.PostRepository;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class PostQueryService implements PostQueryUseCase {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private final PostRepository postRepository;

    public PostQueryService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public Post findById(Long id) {
        return postRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("There is no Post which has id " + id));
    }

    @Override
    public List<Post> findByPageForTitle(String title, int page) {
        Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE);

        return postRepository.findAll(pageable).getContent();
    }
}
