package com.fasttime.domain.review.service;

import com.fasttime.domain.bootcamp.repository.BootCampRepository;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.exception.MemberNotFoundException;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.review.dto.request.ReviewRequestDTO;
import com.fasttime.domain.review.dto.response.BootcampReviewSummaryDTO;
import com.fasttime.domain.review.dto.response.ReviewResponseDTO;
import com.fasttime.domain.review.dto.response.TagSummaryDTO;
import com.fasttime.domain.review.entity.Review;
import com.fasttime.domain.review.entity.ReviewTag;
import com.fasttime.domain.review.entity.Tag;
import com.fasttime.domain.review.exception.BootCampNotFoundException;
import com.fasttime.domain.review.exception.ReviewAlreadyDeletedException;
import com.fasttime.domain.review.exception.ReviewAlreadyExistsException;
import com.fasttime.domain.review.exception.ReviewNotFoundException;
import com.fasttime.domain.review.exception.TagNotFoundException;
import com.fasttime.domain.review.exception.UnauthorizedAccessException;
import com.fasttime.domain.review.repository.ReviewRepository;
import com.fasttime.domain.review.repository.ReviewTagRepository;
import com.fasttime.domain.review.repository.TagRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewTagRepository reviewTagRepository;
    private final TagRepository tagRepository;
    private final MemberRepository memberRepository;
    private final BootCampRepository bootCampRepository;

    public Review createReview(ReviewRequestDTO requestDTO, Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(MemberNotFoundException::new);
        if (!member.isCampCrtfc()) {
            throw new UnauthorizedAccessException();
        }

        Review existingReview = reviewRepository.findByMemberId(memberId);
        if (existingReview != null) {
            if (existingReview.isDeleted()) {
                reviewTagRepository.deleteByReview(existingReview);
                updateReview(existingReview, requestDTO);
                existingReview.restore();
                return reviewRepository.save(existingReview);
            } else {
                throw new ReviewAlreadyExistsException();
            }
        }

        Review newReview = requestDTO.createReview(member);
        updateReviewTags(newReview, requestDTO);
        return reviewRepository.save(newReview);
    }

    public ReviewResponseDTO createAndReturnReviewResponse(ReviewRequestDTO requestDTO,
        Long memberId) {
        Review review = createReview(requestDTO, memberId);

        Set<String> goodTagContents = getTagContents(requestDTO.goodtags());
        Set<String> badTagContents = getTagContents(requestDTO.badtags());

        return ReviewResponseDTO.of(review, goodTagContents, badTagContents);
    }

    private Set<String> getTagContents(Set<Long> tagIds) {
        return tagIds.stream()
            .map(tagId -> tagRepository.findById(tagId)
                .map(Tag::getContent)
                .orElseThrow(TagNotFoundException::new))
            .collect(Collectors.toSet());
    }

    public void deleteReview(Long reviewId, Long memberId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(ReviewNotFoundException::new);
        if (!review.getMember().getId().equals(memberId)) {
            throw new UnauthorizedAccessException();
        }
        if (review.isDeleted()) {
            throw new ReviewAlreadyDeletedException();
        }

        review.softDelete();
        reviewRepository.save(review);
    }

    public Review updateReview(Long reviewId, ReviewRequestDTO requestDTO, Long memberId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(ReviewNotFoundException::new);
        if (!review.getMember().getId().equals(memberId)) {
            throw new UnauthorizedAccessException();
        }
        reviewTagRepository.deleteByReview(review);

        updateReview(review, requestDTO);
        return reviewRepository.save(review);
    }

    private void updateReview(Review review, ReviewRequestDTO requestDTO) {
        review.updateReviewDetails(requestDTO.title(), requestDTO.rating(), requestDTO.content());
        updateReviewTags(review, requestDTO);
    }

    private void updateReviewTags(Review review, ReviewRequestDTO requestDTO) {
        Set<ReviewTag> newReviewTags = new HashSet<>();
        newReviewTags.addAll(createReviewTags(requestDTO.goodtags(), review));
        newReviewTags.addAll(createReviewTags(requestDTO.badtags(), review));
        review.setReviewTags(newReviewTags);
    }

    private Set<ReviewTag> createReviewTags(Set<Long> tagIds, Review review) {
        return tagIds.stream()
            .map(tagId -> tagRepository.findById(tagId)
                .orElseThrow(TagNotFoundException::new))
            .map(tag -> new ReviewTag(null, review, tag))
            .collect(Collectors.toSet());
    }

    public ReviewResponseDTO updateAndReturnReviewResponse(Long reviewId,
        ReviewRequestDTO requestDTO, Long memberId) {
        Review updatedReview = updateReview(reviewId, requestDTO, memberId);
        Set<String> goodTagContents = getTagContents(requestDTO.goodtags());
        Set<String> badTagContents = getTagContents(requestDTO.badtags());
        return ReviewResponseDTO.of(updatedReview, goodTagContents, badTagContents);
    }

    public List<ReviewResponseDTO> getSortedReviews(String sortBy, String bootcamp) {
        Sort sort = sortBy.equals("rating") ? Sort.by("rating").descending()
            : Sort.by("createdAt").descending();

        if (bootcamp != null && !bootcamp.isEmpty()) {
            boolean exists = bootCampRepository.existsByName(bootcamp);
            if (!exists) {
                throw new BootCampNotFoundException();
            }
            List<Review> reviews = reviewRepository.findByBootcampName(bootcamp, sort);
            return reviews.stream().map(this::convertToReviewResponseDTO)
                .collect(Collectors.toList());
        } else {
            List<Review> reviews = reviewRepository.findAll(sort);
            return reviews.stream().map(this::convertToReviewResponseDTO)
                .collect(Collectors.toList());
        }
    }

    private ReviewResponseDTO convertToReviewResponseDTO(Review review) {
        String bootcampName = review.getBootCamp().getName();
        Set<String> goodTagContents = extractTagContents(review, true);
        Set<String> badTagContents = extractTagContents(review, false);
        String authorNickname = review.getMember().getNickname();
        return new ReviewResponseDTO(
            review.getId(), authorNickname, bootcampName, review.getTitle(),
            goodTagContents, badTagContents, review.getRating(), review.getContent()
        );
    }

    private Set<String> extractTagContents(Review review, boolean isGoodTag) {
        return review.getReviewTags().stream()
            .filter(reviewTag -> reviewTag.getTag().isGoodTag() == isGoodTag)
            .map(ReviewTag::getTag)
            .map(Tag::getContent)
            .collect(Collectors.toSet());
    }

    public List<BootcampReviewSummaryDTO> getBootcampReviewSummaries() {
        List<String> bootcamps = reviewRepository.findAllBootcamps();
        List<BootcampReviewSummaryDTO> summaries = new ArrayList<>();

        for (String bootcamp : bootcamps) {
            double averageRating = reviewRepository.findAverageRatingByBootcamp(bootcamp);
            int totalReviews = reviewRepository.countByBootcamp(bootcamp);

            summaries.add(new BootcampReviewSummaryDTO(bootcamp, averageRating, totalReviews));
        }
        return summaries;
    }

    public TagSummaryDTO getBootcampTagData(String bootcamp) {

        boolean exists = bootCampRepository.existsByName(bootcamp);
        if (!exists) {
            throw new BootCampNotFoundException();
        }

        List<Object[]> tagCountsArray = reviewTagRepository.countTagsByBootcampGroupedByTagId(
            bootcamp);
        Map<Long, Long> tagCounts = new HashMap<>();
        int totalTags = 0;

        for (Object[] count : tagCountsArray) {
            Long tagId = (Long) count[0];
            Long countValue = (Long) count[1];
            tagCounts.put(tagId, countValue);
            totalTags += countValue;
        }

        return new TagSummaryDTO(totalTags, tagCounts);
    }
}
