package com.fasttime.domain.post.service;

import com.fasttime.domain.post.dto.service.response.PostListResponseDto;
import com.fasttime.domain.post.dto.service.response.PostResponseDto;
import com.fasttime.domain.post.entity.Post;
import com.fasttime.domain.post.repository.PostRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class PostQueryService {

    private final PostRepository postRepository;

    public PostQueryService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public PostResponseDto searchById(Long id) {
        Post findPost = postRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));

        return PostResponseDto.of(findPost);
    }

    public List<PostListResponseDto> searchPosts(Pageable pageable) {
        return postRepository.findAll(pageable)
            .stream()
            .map(PostListResponseDto::of)
            .collect(Collectors.toList());
    }
}
