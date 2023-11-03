package com.fasttime.domain.article.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ArticleSettingProvider {

    @Value("${article.anonymous.nickname:anonymous}")
    private String anonymousNickname;

    @Value("${article.default.orderField:createdAt}")
    private String defaultOrderField;
}
