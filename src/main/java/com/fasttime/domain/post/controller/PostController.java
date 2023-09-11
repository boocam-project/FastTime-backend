package com.fasttime.domain.post.controller;

import com.fasttime.domain.post.dto.controller.request.PostCreateRequestDto;
import com.fasttime.domain.post.dto.service.request.PostCreateServiceDto;
import com.fasttime.domain.post.dto.service.response.PostDetailResponseDto;
import com.fasttime.domain.post.service.PostCommandService;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/v1/post")
@RestController
public class PostController {

    private final PostCommandService postCommandService;

    public PostController(PostCommandService postCommandService) {
        this.postCommandService = postCommandService;
    }

    @PostMapping
    public ResponseEntity<PostDetailResponseDto> writePost(@Valid @RequestBody PostCreateRequestDto requestDto) {
        PostDetailResponseDto postResponseDto = postCommandService.writePost(
            new PostCreateServiceDto(requestDto.getMemberId(),
                requestDto.getTitle(),
                requestDto.getContent(),
                requestDto.isAnonymity()));

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(postResponseDto);
    }
}
