package com.fasttime.domain.reference.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String organization;

    private String corporateType;

    private String participate;

    private LocalDate startDate;

    private LocalDate endDate;

    private String period;

    private int recruitment;

    private String area;

    private String preferredSkill;

    private String homepageUrl;

    private String field;

    private String activityBenefit;

    private String bonusBenefit;

    @Column(columnDefinition = "TEXT", length = 1000)
    private String description;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum('BEFORE', 'DURING', 'CLOSED')")
    private RecruitmentStatus status;

    @Builder
    private Activity(
        Long id,
        String title,
        String organization,
        String corporateType,
        String participate,
        LocalDate startDate,
        LocalDate endDate,
        String period,
        int recruitment,
        String area,
        String preferredSkill,
        String homepageUrl,
        String field,
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
        this.startDate = startDate;
        this.endDate = endDate;
        this.period = period;
        this.recruitment = recruitment;
        this.area = area;
        this.preferredSkill = preferredSkill;
        this.homepageUrl = homepageUrl;
        this.field = field;
        this.activityBenefit = activityBenefit;
        this.bonusBenefit = bonusBenefit;
        this.description = description;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    public void statusUpdate(RecruitmentStatus status) {
        this.status = status;
    }

    public long getDDay() {
        return ChronoUnit.DAYS.between(LocalDate.now(), this.getEndDate());
    }
}
