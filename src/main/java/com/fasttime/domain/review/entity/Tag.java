package com.fasttime.domain.review.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "tag")
    private Set<ReviewTag> reviewTags = new HashSet<>();

    private String content;
    @Column(name = "is_good_tag")
    private boolean isGoodTag;

    public static Tag create(String content) {
        return new Tag(content);
    }

    protected Tag(String content) {
        this.content = content;
    }

}