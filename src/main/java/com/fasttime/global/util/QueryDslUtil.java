package com.fasttime.global.util;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;

/**
 * QueryDSL에서 사용하기 위한 Utility Class
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
public class QueryDslUtil {

    /**
     * QueryDSL 정렬을 위한 OrderSpecifier 객체 생성 메서드
     *
     * @param order     오름차순 or 내림차순
     * @param parent    qClass 경로
     * @param fieldName 정렬 기준 필드
     * @return OderSpecifier 객체
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    public static OrderSpecifier<?> getSortedColumn(Order order, Path<?> parent, String fieldName) {
        Path<Object> fieldPath = Expressions.path(Object.class, parent, fieldName);
        return new OrderSpecifier(order, fieldPath);
    }
}
