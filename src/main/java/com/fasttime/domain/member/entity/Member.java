package com.fasttime.domain.member.entity;

import com.fasttime.global.common.BaseTimeEntity;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
@Table(name = "Member")
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String nickname;

    @Override
    public void delete(LocalDateTime currentTime) {
        super.delete(currentTime);
    }

    @Override
    public void restore() {
        super.restore();
    }

    @Column(name = "image", columnDefinition = "TEXT")
    private String image;

    public void update(String nickname, String image) {
        this.nickname = nickname;
        this.image = image;

    }


}
