package com.fasttime.domain.comment.controller;

import com.fasttime.domain.comment.dto.request.CreateCommentRequest;
import com.fasttime.domain.comment.dto.request.DeleteCommentRequest;
import com.fasttime.domain.comment.dto.request.UpdateCommentRequest;
import com.fasttime.domain.comment.service.CommentService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/v1/comment")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public String create(@Valid @RequestBody CreateCommentRequest createCommentRequest,
        Model model) {
        log.info("CreateCommentRequest: " + createCommentRequest);
        model.addAttribute(commentService.createComment(createCommentRequest));
        return "registerCommentSuccess";
    }

    @PostMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public String update(@Valid @RequestBody UpdateCommentRequest updateCommentRequest,
        Model model) {
        log.info("UpdateCommentRequest: " + updateCommentRequest);
        model.addAttribute(commentService.updateComment(updateCommentRequest));
        return "updateCommentSuccess";
    }

    @PostMapping("/delete")
    @ResponseStatus(HttpStatus.OK)
    public String delete(@Valid @RequestBody DeleteCommentRequest deleteCommentRequest,
        Model model) {
        log.info("DeleteCommentRequest: " + deleteCommentRequest);
        model.addAttribute(commentService.deleteComment(deleteCommentRequest));
        return "removeCommentSuccess";
    }
}
