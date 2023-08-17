package com.fasttime.domain.post.controller;

import com.fasttime.domain.post.dto.controller.request.PostCreateRequestDto;
import com.fasttime.domain.post.dto.usecase.request.PostCreateServiceDto;
import com.fasttime.domain.post.service.PostService;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequestMapping("/api/v1/post")
@Controller
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public String writePost(@Valid @ModelAttribute PostCreateRequestDto requestDto,
        BindingResult bindingResult) {
        log.info("request :" + requestDto);
        postService.writePost(
            new PostCreateServiceDto(requestDto.getMemberId(),
                requestDto.getTitle(),
                requestDto.getContent(),
                requestDto.isAnounumity()));

        return "ok";
    }
}
