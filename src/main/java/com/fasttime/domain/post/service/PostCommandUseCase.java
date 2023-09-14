package com.fasttime.domain.post.service;

import com.fasttime.domain.post.dto.service.request.PostCreateServiceDto;
import com.fasttime.domain.post.dto.service.request.PostDeleteServiceDto;
import com.fasttime.domain.post.dto.service.request.PostUpdateServiceDto;
import com.fasttime.domain.post.dto.service.response.PostResponseDto;
import com.fasttime.domain.post.entity.Post;

public interface PostCommandUseCase {

    Post writePost(PostCreateServiceDto serviceDto);

    PostResponseDto updatePost(PostUpdateServiceDto serviceDto);

    void deletePost(PostDeleteServiceDto serviceDto);
}
