package com.fasttime.domain.post.controller;

import com.fasttime.domain.post.dto.controller.request.PostCreateRequestDto;
import com.fasttime.domain.post.dto.service.request.PostCreateServiceDto;
import com.fasttime.domain.post.service.PostCommandService;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@RequestMapping("/api/v1/post")
@Controller
public class PostController {

    private final PostCommandService postCommandService;

    public PostController(PostCommandService postCommandService) {
        this.postCommandService = postCommandService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public String writePost(@Valid @ModelAttribute PostCreateRequestDto requestDto,
        Model model,
        BindingResult bindingResult) {
        log.info("request :" + requestDto);
        postCommandService.writePost(
            new PostCreateServiceDto(requestDto.getMemberId(),
                requestDto.getTitle(),
                requestDto.getContent(),
                requestDto.isAnonymity()));

        return "post/posts";
    }
}
