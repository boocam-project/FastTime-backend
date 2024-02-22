package com.fasttime.domain.review.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasttime.domain.review.dto.response.TagSummaryDTO;
import com.fasttime.domain.review.exception.BootCampNotFoundException;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.HashSet;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.entity.Role;
import com.fasttime.domain.member.exception.MemberNotFoundException;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.review.dto.request.ReviewRequestDTO;
import com.fasttime.domain.review.dto.response.ReviewResponseDTO;
import com.fasttime.domain.review.entity.Review;
import com.fasttime.domain.review.exception.ReviewNotFoundException;
import com.fasttime.domain.review.repository.ReviewRepository;
import com.fasttime.domain.review.repository.ReviewTagRepository;
import com.fasttime.domain.review.service.ReviewService;
import com.fasttime.domain.review.dto.response.BootcampReviewSummaryDTO;
import com.fasttime.domain.review.exception.UnauthorizedAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

@Transactional
@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ReviewTagRepository reviewTagRepository;

    private Member member;

    private ReviewRequestDTO reviewRequestDTO;

    @BeforeEach
    void setUp() {
        member = Member.builder()
            .id(1L)
            .email("test@example.com")
            .password("password")
            .nickname("nickname")
            .campCrtfc(true)
            .bootcamp("패스트캠퍼스X야놀자 부트캠프")
            .role(Role.ROLE_USER)
            .build();
        reviewRequestDTO = new ReviewRequestDTO(
            "테스트 리뷰",
            new HashSet<>(),
            new HashSet<>(),
            5,
            "좋아용"
        );
    }

    @Nested
    @DisplayName("createAndReturnReviewResponse()는 ")
    class Context_getComments {

        @Test
        @DisplayName("리뷰 작성에 성공한다.")
        void new_create_willSuccess() {
            // given
            given(memberRepository.findById(any(Long.class))).willReturn(Optional.of(member));
            given(reviewRepository.save(any(Review.class))).willReturn(
                reviewRequestDTO.createReview(member));
            // when
            ReviewResponseDTO result = reviewService.createAndReturnReviewResponse(reviewRequestDTO,
                1L);
            // then
            assertThat(result).isNotNull();
            assertThat(result.title()).isEqualTo("테스트 리뷰");
            assertThat(result.rating()).isEqualTo(5);
            assertThat(result.content()).isEqualTo("좋아용");

            // Verify interactions
            verify(memberRepository, times(1)).findById(any(Long.class));
            verify(reviewRepository, times(1)).save(any(Review.class));
        }

        @Test
        @DisplayName("권한이 없을 경우 실패한다.")
        void Unauthorized_willFail() {
            // given
            Member unauthorizedMember = Member.builder()
                .id(member.getId())
                .email(member.getEmail())
                .password(member.getPassword())
                .nickname(member.getNickname())
                .campCrtfc(false)
                .bootcamp(member.getBootcamp())
                .role(member.getRole())
                .build();

            given(memberRepository.findById(anyLong())).willReturn(Optional.of(unauthorizedMember));

            // when, then
            assertThrows(UnauthorizedAccessException.class, () -> {
                reviewService.createReview(reviewRequestDTO, unauthorizedMember.getId());
            });
        }

        @Test
        @DisplayName("존재하지 않은 사용자가 시도하면 실패한다.")
        void NotFound_willFail() {
            // given
            given(memberRepository.findById(any(Long.class))).willReturn(Optional.empty());

            // when, then
            assertThrows(MemberNotFoundException.class, () -> {
                reviewService.createAndReturnReviewResponse(reviewRequestDTO, 1L);
            });
        }
    }

    @Nested
    @DisplayName("deleteReview()는")
    class Context_deleteReview {

        @Test
        @DisplayName("성공한다.")
        void deleteReview_willSuccess() {
            // given
            Review mockReview = Mockito.mock(Review.class);
            given(reviewRepository.findById(anyLong())).willReturn(Optional.of(mockReview));
            given(mockReview.getMember()).willReturn(member);

            // when
            reviewService.deleteReview(1L, member.getId());

            // then
            verify(reviewRepository, times(1)).save(mockReview);
        }

        @Test
        @DisplayName("리뷰를 찾을 수 없으면 실패한다.")
        void NotFound_willFail() {
            // given
            given(reviewRepository.findById(anyLong())).willReturn(Optional.empty());

            // when, then
            assertThrows(ReviewNotFoundException.class, () -> {
                reviewService.deleteReview(1L, member.getId());
            });
        }

        @Test
        @DisplayName("권한이 없는 사용자는 실패한다.")
        void Unauthorized_willFail() {
            // given
            Review mockReview = Mockito.mock(Review.class);
            Member otherMember = Member.builder().id(2L).build();
            given(reviewRepository.findById(anyLong())).willReturn(Optional.of(mockReview));
            given(mockReview.getMember()).willReturn(otherMember);

            // when, then
            assertThrows(UnauthorizedAccessException.class, () -> {
                reviewService.deleteReview(1L, member.getId());
            });
        }
    }

    @Nested
    @DisplayName("updateReview()는")
    class Context_updateReview {

        @Test
        @DisplayName("리뷰 업데이트 성공한다.")
        void updateReview_willSuccess() {
            // given
            Review mockReview = Mockito.mock(Review.class);
            given(reviewRepository.findById(anyLong())).willReturn(Optional.of(mockReview));
            given(mockReview.getMember()).willReturn(member);

            // when
            reviewService.updateReview(mockReview.getId(), reviewRequestDTO, member.getId());

            // then
            verify(mockReview, times(1)).updateReviewDetails(anyString(), anyInt(), anyString());
            verify(reviewTagRepository, times(1)).deleteByReview(any(Review.class));
            verify(reviewRepository, times(1)).save(any(Review.class));
        }

        @Test
        @DisplayName("리뷰를 찾을 수 없으면 실패한다.")
        void NotFound_willFail() {
            // given
            given(reviewRepository.findById(anyLong())).willReturn(Optional.empty());

            // when, then
            assertThrows(ReviewNotFoundException.class, () -> {
                reviewService.updateReview(1L, reviewRequestDTO, member.getId());
            });
        }

        @Test
        @DisplayName("권한이 없는 사용자는 실패한다.")
        void Unauthorized_willFail() {
            // given
            Review existingReview = Mockito.mock(Review.class);
            Member otherMember = Member.builder().id(2L).build();
            given(reviewRepository.findById(anyLong())).willReturn(Optional.of(existingReview));
            given(existingReview.getMember()).willReturn(otherMember);

            // when, then
            assertThrows(UnauthorizedAccessException.class, () -> {
                reviewService.updateReview(1L, reviewRequestDTO, member.getId());
            });
        }
    }

    @Nested
    @DisplayName("getSortedReviews()는")
    class Context_getSortedReviews {

        @Test
        @DisplayName("모든 리뷰를 정렬 기준에 따라 조회한다.")
        void withoutBootcampFilter_willSuccess() {
            // given
            List<Review> mockReviews = createMockReviews();
            given(reviewRepository.findAll(any(Sort.class))).willReturn(mockReviews);

            // when
            List<ReviewResponseDTO> result = reviewService.getSortedReviews("createdAt", null);

            // then
            assertThat(result).hasSize(mockReviews.size());
            verify(reviewRepository, times(1)).findAll(any(Sort.class));
        }

        @Test
        @DisplayName("부트캠프별 리뷰를 정렬 기준에 따라 조회한다.")
        void withBootcampFilter_willSuccess() {
            // given
            String bootcampName = "부트캠프1";
            given(memberRepository.existsByBootcamp(bootcampName)).willReturn(true); // 부트캠프 존재 확인
            List<Review> mockReviews = createMockReviewsForBootcamp(bootcampName);
            given(reviewRepository.findByBootcamp(bootcampName,
                Sort.by("createdAt").descending())).willReturn(
                mockReviews);

            // when
            List<ReviewResponseDTO> result = reviewService.getSortedReviews("createdAt",
                bootcampName);

            // then
            assertThat(result).hasSize(mockReviews.size());
            assertThat(result.get(0).bootcamp()).isEqualTo(bootcampName);
        }

        private List<Review> createMockReviews() {
            Review review1 = new Review(1L, "리뷰 1", "부트캠프1", 5, "내용 1", new HashSet<>(), member);
            Review review2 = new Review(2L, "리뷰 2", "부트캠프2", 4, "내용 2", new HashSet<>(), member);
            return List.of(review1, review2);
        }

        private List<Review> createMockReviewsForBootcamp(String bootcampName) {
            Review review1 = new Review(1L, "리뷰 1", bootcampName, 5, "내용 1", new HashSet<>(),
                member);
            Review review2 = new Review(2L, "리뷰 2", bootcampName, 4, "내용 2", new HashSet<>(),
                member);
            return List.of(review1, review2);
        }
    }

    @Nested
    @DisplayName("getBootcampReviewSummaries()는")
    class Context_getBootcampReviewSummaries {

        @Test
        @DisplayName("부트캠프별 리뷰 요약 정보를 조회한다.")
        void getBootcampReviewSummaries_willSuccess() {
            // given
            List<String> bootcamps = List.of("부트캠프1", "부트캠프2");
            given(reviewRepository.findAllBootcamps()).willReturn(bootcamps);
            given(reviewRepository.findAverageRatingByBootcamp(anyString())).willReturn(4.5);
            given(reviewRepository.countByBootcamp(anyString())).willReturn(10);

            // when
            List<BootcampReviewSummaryDTO> result = reviewService.getBootcampReviewSummaries();

            // then
            assertThat(result).hasSize(bootcamps.size());
            assertThat(result.get(0).bootcamp()).isEqualTo(bootcamps.get(0));
            assertThat(result.get(0).averageRating()).isEqualTo(4.5);
            assertThat(result.get(0).totalReviews()).isEqualTo(10);

            // Verify interactions
            verify(reviewRepository, times(1)).findAllBootcamps();
            verify(reviewRepository, times(2)).findAverageRatingByBootcamp(anyString());
            verify(reviewRepository, times(2)).countByBootcamp(anyString());
        }
    }

    @Test
    @DisplayName("부트캠프별 리뷰 요약 정보가 없을 경우 처리한다.")
    void NoData_willHandle() {
        // given
        given(reviewRepository.findAllBootcamps()).willReturn(new ArrayList<>());

        // when
        List<BootcampReviewSummaryDTO> result = reviewService.getBootcampReviewSummaries();

        // then
        assertThat(result).isEmpty();
    }

    @Nested
    @DisplayName("getBootcampTagData()는")
    class Context_getBootcampTagData {

        @Test
        @DisplayName("부트캠프별 태그 데이터를 성공적으로 조회한다.")
        void _willSuccess() {
            // given
            String bootcampName = "부트캠프1";
            given(memberRepository.existsByBootcamp(bootcampName)).willReturn(true); // 부트캠프 존재 확인
            given(reviewTagRepository.countTagsByBootcampGroupedByTagId(bootcampName)).willReturn(
                List.of(new Object[]{1L, 5L}, new Object[]{2L, 3L})
            );

            // when
            TagSummaryDTO result = reviewService.getBootcampTagData(bootcampName);

            // then
            assertThat(result.totalTags()).isEqualTo(8);
            assertThat(result.tagCounts().size()).isEqualTo(2);
            assertThat(result.tagCounts().get(1L)).isEqualTo(5L);
            assertThat(result.tagCounts().get(2L)).isEqualTo(3L);
        }

        @Test
        @DisplayName("부트캠프가 존재하지 않을 경우 예외를 발생시킨다.")
        void NotFound_willFail() {
            // given
            String bootcampName = "존재하지 않는 부트캠프";
            given(memberRepository.existsByBootcamp(bootcampName)).willReturn(
                false);

            // when, then
            assertThrows(BootCampNotFoundException.class, () -> {
                reviewService.getBootcampTagData(bootcampName);
            });
        }
    }
}
