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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/v1/comment")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/create")
    public ModelAndView create(@Valid @RequestBody CreateCommentRequest createCommentRequest,
        ModelAndView mv) {
        log.info("CreateCommentRequest: " + createCommentRequest);
        mv.setStatus(HttpStatus.CREATED);
        mv.addObject(commentService.createComment(createCommentRequest));
        mv.setViewName("registerCommentSuccess");
        return mv;
    }

    @PostMapping("/delete")
    public ModelAndView delete(@Valid @RequestBody DeleteCommentRequest deleteCommentRequest,
        ModelAndView mv) {
        log.info("DeleteCommentRequest: " + deleteCommentRequest);
        mv.setStatus(HttpStatus.OK);
        mv.addObject(commentService.deleteComment(deleteCommentRequest));
        mv.setViewName("removeCommentSuccess");
        return mv;
    }

    @PostMapping("/update")
    public ModelAndView update(@Valid @RequestBody UpdateCommentRequest updateCommentRequest,
        ModelAndView mv) {
        log.info("UpdateCommentRequest: " + updateCommentRequest);
        mv.setStatus(HttpStatus.OK);
        mv.addObject(commentService.updateComment(updateCommentRequest));
        mv.setViewName("updateCommentSuccess");
        return mv;
    }
}
