package com.fasttime.domain.member.entity;

import com.fasttime.global.common.BaseTimeEntity;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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


    @Column(name = "deleted_at") // deletedAt 칼럼 추가
    private LocalDateTime deletedAt;


    @Column(name = "image", columnDefinition = "TEXT") // image 칼럼 추가
    private String image;

    @CreationTimestamp // created_at 필드 자동 갱신
    @Column(name = "created_at")
    private LocalDateTime createdAt;


    @UpdateTimestamp // updated_at 필드 자동 갱신
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
