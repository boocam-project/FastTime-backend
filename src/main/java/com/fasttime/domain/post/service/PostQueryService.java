package com.fasttime.domain.post.service;

import com.fasttime.domain.post.dto.service.response.PostDetailResponseDto;
import com.fasttime.domain.post.dto.service.response.PostsResponseDto;
import com.fasttime.domain.post.exception.PostNotFoundException;
import com.fasttime.domain.post.repository.PostRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class PostQueryService implements PostQueryUseCase {

    private final PostRepository postRepository;

    public PostQueryService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public PostDetailResponseDto findById(Long id) {
        return PostDetailResponseDto.entityToDto(postRepository.findById(id)
            .orElseThrow(PostNotFoundException::new));
    }

    @Override
    public List<PostsResponseDto> searchPost(PostSearchCondition postSearchCondition) {
        return postRepository.search(postSearchCondition)
            .stream()
            .map(repositoryDto -> PostsResponseDto.builder()
                .id(repositoryDto.getId())
                .title(repositoryDto.getTitle())
                .nickname(repositoryDto.getNickname())
                .anonymity(repositoryDto.isAnonymity())
                .likeCount(repositoryDto.getLikeCount())
                .hateCount(repositoryDto.getHateCount())
                .createdAt(repositoryDto.getCreatedAt())
                .createdAt(repositoryDto.getLastModifiedAt())
                .build())
            .collect(Collectors.toList());
    }
}
