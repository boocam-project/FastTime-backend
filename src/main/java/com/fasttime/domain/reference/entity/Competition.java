package com.fasttime.domain.reference.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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

    @NotBlank
    private String title;

    @NotNull
    private String organization;

    @NotNull
    private String corporateType;

    @NotNull
    private String participate;

    @NotNull
    private String awardScale;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotNull
    private String homepageUrl;

    @NotNull
    private String activityBenefit;

    @NotNull
    private String bonusBenefit;

    @NotNull
    @Size(max = 1000)
    @Column(columnDefinition = "TEXT", length = 1000)
    private String description;

    @NotNull
    private String imageUrl;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum('BEFORE', 'DURING', 'CLOSED')")
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

    public void statusUpdate(RecruitmentStatus status) {
        this.status = status;
    }

    public long getDDay() {
        return ChronoUnit.DAYS.between(LocalDate.now(), this.getEndDate());
    }
}
