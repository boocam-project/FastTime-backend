package com.fasttime.domain.post.service;

import com.fasttime.domain.post.dto.service.request.PostCreateServiceDto;
import com.fasttime.domain.post.dto.service.request.PostUpdateServiceDto;
import com.fasttime.domain.post.dto.service.response.PostResponseDto;
import com.fasttime.domain.post.entity.Post;
import com.fasttime.domain.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class PostCommandService {

    private final PostRepository postRepository;

    public PostCommandService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public PostResponseDto writePost(PostCreateServiceDto serviceDto) {

        Post createdPost = Post.createNewPost(null, serviceDto.getTitle(), serviceDto.getContent(),
            false);

        Post savedPost = postRepository.save(createdPost);

        return PostResponseDto.of(savedPost);
    }

    public PostResponseDto updatePost(PostUpdateServiceDto serviceDto) {

        Post post = postRepository.findById(serviceDto.getPostId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        validateMemberAuthority(serviceDto.getMemberId(), post.getMember().getId());

        post.update(serviceDto.getContent());

        return PostResponseDto.of(post);
    }

    private static void validateMemberAuthority(Long requesterId, Long writerId) {
        if (!requesterId.equals(writerId)) {
            throw new IllegalArgumentException("해당 게시글에 대한 권한이 없습니다.");
        }
    }
}
