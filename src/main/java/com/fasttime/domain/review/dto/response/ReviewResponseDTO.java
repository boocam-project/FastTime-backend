package com.fasttime.domain.review.dto.response;

import com.fasttime.domain.review.entity.Review;
import com.fasttime.domain.review.entity.Tag;
import com.fasttime.domain.review.repository.TagRepository;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public record ReviewResponseDTO(
    Long id,
    String bootcamp,
    String title,
    Set<String> goodtags,
    Set<String> badtags,
    int rating,
    String content) {

    public static ReviewResponseDTO of(Review review, Set<Long> goodTagIds, Set<Long> badTagIds, TagRepository tagRepository) {
        Set<String> goodTagContents = getTagContents(goodTagIds, tagRepository);
        Set<String> badTagContents = getTagContents(badTagIds, tagRepository);

        return new ReviewResponseDTO(
            review.getId(),
            review.getBootcamp(),
            review.getTitle(),
            goodTagContents,
            badTagContents,
            review.getRating(),
            review.getContent());
    }

    private static Set<String> getTagContents(Set<Long> tagIds, TagRepository tagRepository) {
        return tagIds.stream()
            .map(tagId -> tagRepository.findById(tagId)
                .map(Tag::getContent)
                .orElse(null))
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }
}
