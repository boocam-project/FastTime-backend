package com.fasttime.domain.review.service;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.exception.MemberNotFoundException;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.review.dto.request.ReviewRequestDTO;
import com.fasttime.domain.review.entity.Review;
import com.fasttime.domain.review.entity.ReviewTag;
import com.fasttime.domain.review.entity.Tag;
import com.fasttime.domain.review.exception.ReviewAlreadyDeletedException;
import com.fasttime.domain.review.exception.ReviewNotFoundException;
import com.fasttime.domain.review.exception.TagNotFoundException;
import com.fasttime.domain.review.repository.ReviewRepository;
import com.fasttime.domain.review.repository.TagRepository;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final TagRepository tagRepository;
    private final MemberRepository memberRepository;

    public Review createReview(ReviewRequestDTO requestDTO, Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberNotFoundException());

        Review review = requestDTO.createReview(member);
        Set<ReviewTag> allReviewTags = processAllTags(requestDTO, review);

        review.setReviewTags(allReviewTags);
        return reviewRepository.save(review);
    }

    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ReviewNotFoundException());
        if (review.isDeleted()) {
            throw new ReviewAlreadyDeletedException();
        }
        review.softDelete();
        reviewRepository.save(review);
    }

    private Set<ReviewTag> processAllTags(ReviewRequestDTO requestDTO, Review review) {
        Set<ReviewTag> goodReviewTags = processTags(requestDTO.goodtags(), review);
        Set<ReviewTag> badReviewTags = processTags(requestDTO.badtags(), review);
        Set<ReviewTag> allReviewTags = new HashSet<>();
        allReviewTags.addAll(goodReviewTags);
        allReviewTags.addAll(badReviewTags);
        return allReviewTags;
    }

    private Set<ReviewTag> processTags(Set<Long> tagIds, Review review) {
        Set<ReviewTag> reviewTags = new HashSet<>();
        if (tagIds != null) {
            reviewTags = tagIds.stream()
                .map(tagId -> {
                    Tag tag = tagRepository.findById(tagId)
                        .orElseThrow(() -> new TagNotFoundException());
                    return new ReviewTag(null, review, tag);
                })
                .collect(Collectors.toSet());
        }
        return reviewTags;
    }

    public TagRepository getTagRepository() {
        return this.tagRepository;
    }
}

