package com.fasttime.domain.member.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "refresh_token")
public class RefreshToken {
    @Id
    @Comment("member_id")
    private Long id;

    private String token;

    @Builder
    public RefreshToken(Long id, String token) {
        this.id = id;
        this.token = token;
    }

    public void updateValue(String token) {
        this.token = token;
    }


}
