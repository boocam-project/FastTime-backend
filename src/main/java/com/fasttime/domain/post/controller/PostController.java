package com.fasttime.domain.post.controller;

import com.fasttime.domain.post.dto.controller.request.PostCreateRequestDto;
import com.fasttime.domain.post.dto.service.request.PostCreateServiceDto;
import com.fasttime.domain.post.dto.service.response.PostDetailResponseDto;
import com.fasttime.domain.post.dto.service.response.PostsResponseDto;
import com.fasttime.domain.post.entity.Post;
import com.fasttime.domain.post.service.PostCommandService;
import com.fasttime.domain.post.service.PostQueryService;
import com.fasttime.domain.post.service.PostQueryUseCase.PostSearchCondition;
import com.fasttime.global.util.ResponseDTO;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/v1/post")
@RestController
public class PostController {

    private final PostCommandService postCommandService;
    private final PostQueryService postQueryService;

    public PostController(PostCommandService postCommandService,
        PostQueryService postQueryService) {
        this.postCommandService = postCommandService;
        this.postQueryService = postQueryService;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<PostDetailResponseDto>> writePost(
        @RequestBody @Valid PostCreateRequestDto requestDto) {
        Post result = postCommandService.writePost(
            new PostCreateServiceDto(requestDto.getMemberId(),
                requestDto.getTitle(),
                requestDto.getContent(),
                requestDto.isAnonymity()));

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ResponseDTO.res(HttpStatus.CREATED, PostDetailResponseDto.entityToDto(result)));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ResponseDTO<PostDetailResponseDto>> getPost(@PathVariable long postId) {
        Post result = postQueryService.findById(postId);

        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.CREATED,
                PostDetailResponseDto.entityToDto(result))
            );
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<PostsResponseDto>>> getPosts(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String nickname,
        @RequestParam(defaultValue = "0") int likeCount,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(defaultValue = "0") int page) {

        List<PostsResponseDto> serviceResponse = postQueryService.searchPost(
            PostSearchCondition.builder()
                .title(title)
                .nickname(nickname)
                .likeCount(likeCount)
                .pageSize(pageSize)
                .page(page)
                .build());

        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.OK, serviceResponse));
    }
}
