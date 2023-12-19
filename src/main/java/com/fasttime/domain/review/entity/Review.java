package com.fasttime.domain.review.entity;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.global.common.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(1)
    @Max(5)
    private int rating;
    private String content;

    @ManyToMany
    @JoinTable(
        name = "review_tag",
        joinColumns = @JoinColumn(name = "review_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @JoinColumn(name = "member_id")
    @ManyToOne
    private Member member;

}
