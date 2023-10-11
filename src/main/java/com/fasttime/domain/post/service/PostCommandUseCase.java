package com.fasttime.domain.post.service;

import com.fasttime.domain.post.dto.service.request.PostCreateServiceDto;
import com.fasttime.domain.post.dto.service.request.PostDeleteServiceDto;
import com.fasttime.domain.post.dto.service.request.PostUpdateServiceDto;
import com.fasttime.domain.post.dto.service.response.PostDetailResponseDto;

public interface PostCommandUseCase {

    PostDetailResponseDto writePost(PostCreateServiceDto serviceDto);

    PostDetailResponseDto updatePost(PostUpdateServiceDto serviceDto);

    void deletePost(PostDeleteServiceDto serviceDto);
}
