package com.fasttime.domain.article.repository;

import lombok.Getter;

@Getter
public enum OrderBy {

    COMMENT_COUNT("commentCount"),
    LIKE_COUNT("likeCount"),
    CREATED_AT("createdAt"),
    ;


    private final String value;

    OrderBy(String value) {
        this.value = value;
    }

    public static OrderBy of(String value) {
        return switch (value.toLowerCase()) {
            case "commentcount" -> OrderBy.COMMENT_COUNT;
            case "likecount" -> OrderBy.LIKE_COUNT;
            default -> OrderBy.CREATED_AT;
        };
    }
}
