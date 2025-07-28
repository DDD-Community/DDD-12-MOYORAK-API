package com.moyorak.api.review.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;

import com.moyorak.api.review.domain.ReviewServingTime;
import com.moyorak.api.review.domain.ReviewServingTimeFixture;
import com.moyorak.api.review.domain.ReviewWaitingTime;
import com.moyorak.api.review.domain.ReviewWaitingTimeFixture;
import com.moyorak.api.review.dto.ReviewServingTimeResponse;
import com.moyorak.api.review.dto.ReviewWaitingTimeResponse;
import com.moyorak.api.review.dto.ReviewWithUserProjection;
import com.moyorak.api.review.dto.ReviewWithUserProjectionFixture;
import com.moyorak.api.review.repository.ReviewRepository;
import com.moyorak.api.review.repository.ReviewServingTimeRepository;
import com.moyorak.api.review.repository.ReviewWaitingTimeRepository;
import com.moyorak.config.exception.BusinessException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {
    @InjectMocks private ReviewService reviewService;

    @Mock private ReviewRepository reviewRepository;

    @Mock private ReviewServingTimeRepository reviewServingTimeRepository;

    @Mock private ReviewWaitingTimeRepository reviewWaitingTimeRepository;

    @Test
    @DisplayName("팀 레스토랑 ID로 리뷰와 사용자 정보를 조회한다")
    void getReviewWithUserByTeamRestaurantId() {
        // given
        final Long teamRestaurantId = 1L;
        final Pageable pageable = PageRequest.of(0, 10);

        final ReviewWithUserProjection reviewWithUserProjection =
                ReviewWithUserProjectionFixture.defaultFixture();
        final Page<ReviewWithUserProjection> mockPage =
                new PageImpl<>(List.of(reviewWithUserProjection), pageable, 1);

        given(reviewRepository.findReviewWithUserByTeamRestaurantId(teamRestaurantId, pageable))
                .willReturn(mockPage);

        // when
        final Page<ReviewWithUserProjection> result =
                reviewService.getReviewWithUserByTeamRestaurantId(teamRestaurantId, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().name()).isEqualTo("아무개");
        assertThat(result.getContent().getFirst().extraText()).isEqualTo("좋은식당");
    }

    @Nested
    @DisplayName("대기,서빙 시간 조회 시")
    class GetReviewTimeList {
        private final Long servingId = 1L;
        private final Long waitingId = 2L;

        @Test
        @DisplayName("서빙 시간 데이터를 정상적으로 조회한다")
        void getReviewServingTime_success() {
            // given
            final ReviewServingTime servingTime =
                    ReviewServingTimeFixture.fixture(servingId, "10~15분", true);
            given(reviewServingTimeRepository.findAllByUse(true)).willReturn(List.of(servingTime));

            // when
            final List<ReviewServingTimeResponse> result = reviewService.getReviewServingTimeList();

            // then
            assertSoftly(
                    it -> {
                        it.assertThat(result).hasSize(1);
                        it.assertThat(result.get(0).servingTimeId()).isEqualTo(1L);
                        it.assertThat(result.get(0).servingTime()).isEqualTo("10~15분");
                    });
        }

        @Test
        @DisplayName("서빙 시간 데이터가 없으면 예외를 던진다")
        void getReviewServingTime_noData_throwsException() {
            // given
            given(reviewServingTimeRepository.findAllByUse(true)).willReturn(List.of());

            // when & then
            assertThatThrownBy(() -> reviewService.getReviewServingTimeList())
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("서빙 시간 데이터가 없습니다.");
        }

        @Test
        @DisplayName("대기 시간 데이터를 정상적으로 조회한다")
        void getReviewWaitingTime_success() {
            // given
            final ReviewWaitingTime waitingTime =
                    ReviewWaitingTimeFixture.fixture(waitingId, "10~15분", true);
            given(reviewWaitingTimeRepository.findAllByUse(true)).willReturn(List.of(waitingTime));

            // when
            final List<ReviewWaitingTimeResponse> result = reviewService.getReviewWaitingTimeList();

            // then
            assertSoftly(
                    it -> {
                        it.assertThat(result).hasSize(1);
                        it.assertThat(result.get(0).waitingTimeId()).isEqualTo(2L);
                        it.assertThat(result.get(0).waitingTime()).isEqualTo("10~15분");
                    });
        }

        @Test
        @DisplayName("대기 시간 데이터가 없으면 예외를 던진다")
        void getReviewWaitingTime_noData_throwsException() {
            // given
            given(reviewWaitingTimeRepository.findAllByUse(true)).willReturn(List.of());

            // when & then
            assertThatThrownBy(() -> reviewService.getReviewWaitingTimeList())
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("대기 시간 데이터가 없습니다.");
        }
    }
}
