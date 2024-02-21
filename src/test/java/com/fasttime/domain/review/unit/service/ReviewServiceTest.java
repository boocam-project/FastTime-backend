package com.fasttime.domain.review.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        @DisplayName("성공한다.")
        void _willSuccess() {
            // given
            Review existingReview = Mockito.mock(Review.class);
            given(reviewRepository.findById(anyLong())).willReturn(Optional.of(existingReview));
            given(existingReview.getMember()).willReturn(member);

            // when
            reviewService.updateReview(1L, reviewRequestDTO, member.getId());

            // then
            verify(reviewRepository, times(1)).save(existingReview);
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
        void _willSuccess() {
            // given
            List<Review> mockReviews = createMockReviews();
            given(reviewRepository.findAll(any(Sort.class))).willReturn(mockReviews);

            // when
            List<ReviewResponseDTO> result = reviewService.getSortedReviews("createdAt");

            // then
            assertThat(result).hasSize(mockReviews.size());
            assertThat(result.get(0).id()).isEqualTo(mockReviews.get(0).getId());
            assertThat(result.get(1).id()).isEqualTo(mockReviews.get(1).getId());

            // Verify interactions
            verify(reviewRepository, times(1)).findAll(any(Sort.class));
        }

        private List<Review> createMockReviews() {
            Review review1 = new Review(1L, "리뷰 1", "부트캠프1", 5, "내용 1", new HashSet<>(), member);
            Review review2 = new Review(2L, "리뷰 2", "부트캠프2", 4, "내용 2", new HashSet<>(), member);
            return List.of(review1, review2);
        }
    }

    @Nested
    @DisplayName("getReviewsByBootcamp()는")
    class Context_getReviewsByBootcamp {

        @Test
        @DisplayName("부트캠프별 리뷰를 정렬 기준에 따라 조회한다.")
        void _willSuccess() {
            // given
            String bootcampName = "부트캠프1";
            List<Review> mockReviews = createMockReviewsForBootcamp(bootcampName);
            given(reviewRepository.findByBootcamp(anyString(), any(Sort.class))).willReturn(
                mockReviews);

            // when
            List<ReviewResponseDTO> result = reviewService.getReviewsByBootcamp(bootcampName,
                "createdAt");

            // then
            assertThat(result).hasSize(mockReviews.size());
            assertThat(result.get(0).bootcamp()).isEqualTo(bootcampName);
            assertThat(result.get(1).bootcamp()).isEqualTo(bootcampName);

            // Verify interactions
            verify(reviewRepository, times(1)).findByBootcamp(anyString(), any(Sort.class));
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
        void _willSuccess() {
            // given
            List<String> bootcamps = List.of("부트캠프1", "부트캠프2");
            List<BootcampReviewSummaryDTO> summaries = createMockSummaries(bootcamps);
            given(reviewRepository.findAllBootcamps()).willReturn(bootcamps);
            for (String bootcamp : bootcamps) {
                given(reviewRepository.findAverageRatingByBootcamp(bootcamp)).willReturn(4.5);
                given(reviewRepository.countByBootcamp(bootcamp)).willReturn(10);
                given(reviewTagRepository.countByBootcamp(bootcamp)).willReturn(20);
            }

            // when
            List<BootcampReviewSummaryDTO> result = reviewService.getBootcampReviewSummaries();

            // then
            assertThat(result).hasSize(summaries.size());
            assertThat(result.get(0).bootcamp()).isEqualTo(summaries.get(0).bootcamp());
            assertThat(result.get(1).bootcamp()).isEqualTo(summaries.get(1).bootcamp());

            // Verify interactions
            verify(reviewRepository, times(1)).findAllBootcamps();
            for (String bootcamp : bootcamps) {
                verify(reviewRepository, times(1)).findAverageRatingByBootcamp(bootcamp);
                verify(reviewRepository, times(1)).countByBootcamp(bootcamp);
                verify(reviewTagRepository, times(1)).countByBootcamp(bootcamp);
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

        private List<BootcampReviewSummaryDTO> createMockSummaries(List<String> bootcamps) {
            List<BootcampReviewSummaryDTO> summaries = new ArrayList<>();
            for (String bootcamp : bootcamps) {
                summaries.add(
                    new BootcampReviewSummaryDTO(bootcamp, 4.5, 10, 20, Map.of(1L, 5L, 2L, 5L)));
            }
            return summaries;
        }
    }
}
