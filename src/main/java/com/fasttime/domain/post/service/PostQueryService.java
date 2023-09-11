package com.fasttime.domain.post.service;

import com.fasttime.domain.post.dto.service.response.PostDetailResponseDto;
import com.fasttime.domain.post.dto.service.response.PostsResponseDto;
import com.fasttime.domain.post.entity.Post;
import com.fasttime.domain.post.repository.PostRepository;
import java.util.List;
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

    public PostDetailResponseDto searchById(Long id) {
        Post findPost = postRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));

        return PostDetailResponseDto.builder()
            .id(findPost.getId())
            .title(findPost.getTitle())
            .content(findPost.getContent())
            .anonymity(findPost.isAnonymity())
            .likeCount(findPost.getLikeCount())
            .hateCount(findPost.getHateCount())
            .build();
    }

    public List<PostsResponseDto> searchPosts(Pageable pageable) {
        throw new UnsupportedOperationException();
    }
}
