package com.fasttime.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasttime.domain.article.controller.ArticleController;
import com.fasttime.domain.article.service.ArticleCommandService;
import com.fasttime.domain.article.service.ArticleQueryService;
import com.fasttime.domain.member.controller.MemberController;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.global.util.SecurityUtil;
import org.apache.catalina.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = {ArticleController.class, MemberController.class},
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)},
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
    protected SecurityUtil securityUtil;
}
