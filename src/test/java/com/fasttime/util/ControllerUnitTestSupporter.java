package com.fasttime.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasttime.domain.article.controller.ArticleController;
import com.fasttime.domain.article.service.ArticleCommandService;
import com.fasttime.domain.article.service.ArticleQueryService;
import com.fasttime.domain.comment.controller.CommentRestController;
import com.fasttime.domain.comment.service.CommentService;
import com.fasttime.domain.member.controller.AdminController;
import com.fasttime.domain.member.controller.EmailController;
import com.fasttime.domain.member.controller.MemberController;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.member.service.AdminService;
import com.fasttime.domain.member.service.EmailUseCase;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.domain.memberArticleLike.controller.MemberArticleLikeRestController;
import com.fasttime.domain.memberArticleLike.service.MemberArticleLikeService;
import com.fasttime.domain.report.contoller.ReportRestController;
import com.fasttime.domain.report.service.ReportService;
import com.fasttime.domain.review.controller.ReviewController;
import com.fasttime.domain.review.service.ReviewService;
import com.fasttime.global.config.SpringSecurityConfig;
import com.fasttime.global.util.SecurityUtil;
import org.apache.catalina.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = {ArticleController.class, MemberController.class, ReportRestController.class,
    EmailController.class, AdminController.class, MemberArticleLikeRestController.class,
    CommentRestController.class, ReviewController.class},
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, SpringSecurityConfig.class})},
    excludeAutoConfiguration = SecurityAutoConfiguration.class)
public abstract class ControllerUnitTestSupporter {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected ArticleCommandService articleCommandService;

    @MockBean
    protected ArticleQueryService articleQueryService;

    @MockBean
    protected MemberService memberService;

    @MockBean
    protected MemberRepository memberRepository;

    @MockBean
    protected ReportService reportService;

    @MockBean
    protected SecurityUtil securityUtil;

    @MockBean
    protected EmailUseCase emailUseCase;

    @MockBean
    protected AdminService adminService;

    @MockBean
    protected MemberArticleLikeService memberArticleLikeService;

    @MockBean
    protected CommentService commentService;

    @MockBean
    protected ReviewService reviewService;
}
