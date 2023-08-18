package com.fasttime.domain.post.service;

import com.fasttime.domain.post.dto.usecase.request.PostCreateServiceDto;
import com.fasttime.domain.post.dto.usecase.response.PostResponseDto;
import com.fasttime.domain.post.entity.Post;
import com.fasttime.domain.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Transactional
    public PostResponseDto writePost(PostCreateServiceDto serviceDto) {
        // TODO: Validate MemberId

        Post createdPost = Post.createNewPost(null, serviceDto.getTitle(), serviceDto.getContent(),
            false);

        Post savedPost = postRepository.save(createdPost);

        return PostResponseDto.of(savedPost);
    }
}
