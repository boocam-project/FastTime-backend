package com.fasttime.domain.post.controller;

import com.fasttime.domain.post.dto.controller.request.PostCreateRequestDto;
import com.fasttime.domain.post.dto.controller.request.PostDeleteRequestDto;
import com.fasttime.domain.post.dto.controller.request.PostUpdateRequestDto;
import com.fasttime.domain.post.dto.service.response.PostDetailResponseDto;
import com.fasttime.domain.post.dto.service.response.PostsResponseDto;
import com.fasttime.domain.post.service.PostCommandUseCase;
import com.fasttime.domain.post.service.PostCommandUseCase.PostCreateServiceDto;
import com.fasttime.domain.post.service.PostCommandUseCase.PostDeleteServiceDto;
import com.fasttime.domain.post.service.PostCommandUseCase.PostUpdateServiceDto;
import com.fasttime.domain.post.service.PostQueryUseCase;
import com.fasttime.domain.post.service.PostQueryUseCase.PostSearchCondition;
import com.fasttime.global.util.ResponseDTO;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    private final PostCommandUseCase postCommandUseCase;
    private final PostQueryUseCase postQueryUseCase;

    public PostController(PostCommandUseCase postCommandUseCase,
        PostQueryUseCase postQueryUseCase) {
        this.postCommandUseCase = postCommandUseCase;
        this.postQueryUseCase = postQueryUseCase;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<PostDetailResponseDto>> writePost(
        @RequestBody @Valid PostCreateRequestDto requestDto) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ResponseDTO.res(HttpStatus.CREATED, postCommandUseCase.writePost(
                new PostCreateServiceDto(requestDto.getMemberId(),
                    requestDto.getTitle(),
                    requestDto.getContent(),
                    requestDto.isAnonymity()))));
    }

    @PatchMapping
    public ResponseEntity<ResponseDTO<PostDetailResponseDto>> updatePost(
        @RequestBody @Valid PostUpdateRequestDto requestDto) {

        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.OK, postCommandUseCase.updatePost(
                new PostUpdateServiceDto(
                    requestDto.getPostId(),
                    requestDto.getMemberId(),
                    requestDto.getTitle(),
                    requestDto.getContent()
                ))));
    }

    @DeleteMapping
    public ResponseEntity<ResponseDTO<Void>> deletePost(
        @RequestBody @Valid PostDeleteRequestDto requestDto) {

        postCommandUseCase.deletePost(new PostDeleteServiceDto(
            requestDto.getPostId(),
            requestDto.getMemberId(),
            LocalDateTime.now()
        ));
        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.OK, null, null));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ResponseDTO<PostDetailResponseDto>> getPost(@PathVariable long postId) {

        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.OK,
                postQueryUseCase.getPostById(postId)));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<PostsResponseDto>>> getPosts(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String nickname,
        @RequestParam(defaultValue = "0") int likeCount,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(defaultValue = "0") int page) {

        List<PostsResponseDto> serviceResponse = postQueryUseCase.searchPost(
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
