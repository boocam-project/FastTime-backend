package com.fasttime.domain.reference.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Competition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String organization;

    private String corporateType;

    private String participate;

    private String  awardScale;

    private LocalDate startDate;

    private LocalDate endDate;

    private String homepageUrl;

    private String activityBenefit;

    private String bonusBenefit;

    @Column(columnDefinition = "TEXT",length = 1000)
    private String description;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private RecruitmentStatus status;

    @Builder
    private Competition(
        Long id,
        String title,
        String organization,
        String corporateType,
        String participate,
        String awardScale,
        LocalDate startDate,
        LocalDate endDate,
        String homepageUrl,
        String activityBenefit,
        String bonusBenefit,
        String description,
        String imageUrl,
        RecruitmentStatus status
    ) {
        this.id = id;
        this.title = title;
        this.organization = organization;
        this.corporateType = corporateType;
        this.participate = participate;
        this.awardScale = awardScale;
        this.startDate = startDate;
        this.endDate = endDate;
        this.homepageUrl = homepageUrl;
        this.activityBenefit = activityBenefit;
        this.bonusBenefit = bonusBenefit;
        this.description = description;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    public void statusUpdate(RecruitmentStatus status){
        this.status = status;
    }
}
