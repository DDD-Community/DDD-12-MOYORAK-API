package com.moyorak.api.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;

import com.moyorak.api.auth.dto.UserReviewRequest;
import com.moyorak.api.auth.dto.UserReviewRequestFixture;
import com.moyorak.api.auth.dto.UserReviewResponse;
import com.moyorak.api.image.ImageStore;
import com.moyorak.api.review.domain.ReviewServingTime;
import com.moyorak.api.review.domain.ReviewServingTimeFixture;
import com.moyorak.api.review.domain.ReviewWaitingTime;
import com.moyorak.api.review.domain.ReviewWaitingTimeFixture;
import com.moyorak.api.review.dto.ReviewPhotoPath;
import com.moyorak.api.review.dto.ReviewWithUserProjection;
import com.moyorak.api.review.dto.ReviewWithUserProjectionFixture;
import com.moyorak.api.review.service.ReviewPhotoService;
import com.moyorak.api.review.service.ReviewService;
import com.moyorak.global.domain.ListResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
public class UserReviewFacadeTest {
    @InjectMocks private UserReviewFacade userReviewFacade;

    @Mock private ReviewService reviewService;
    @Mock private ReviewPhotoService reviewPhotoService;
    @Mock private ImageStore imageStore;

    @Test
    @DisplayName("성공 하면 리뷰를 반환한다")
    void getTeamRestaurantReviewsSuccess() {
        final Long userId = 1L;
        // given
        final UserReviewRequest request = UserReviewRequestFixture.fixture(1, 10);

        final ReviewWithUserProjection review = ReviewWithUserProjectionFixture.defaultFixture();
        final Page<ReviewWithUserProjection> reviewPage =
                new PageImpl<>(List.of(review), PageRequest.of(0, 10), 1);

        given(reviewService.getReviewWithUserByUserId(userId, request.toPageableAndDateSorted()))
                .willReturn(reviewPage);

        final List<Long> reviewIds = List.of(1L);
        final List<ReviewPhotoPath> photoPaths =
                List.of(new ReviewPhotoPath(1L, "s3://review1/photo1.jpg"));

        given(reviewPhotoService.getReviewPhotoPathsGroupedByReviewId(reviewIds))
                .willReturn(photoPaths);
        given(imageStore.getUrlFromStringPath("s3://review1/photo1.jpg"))
                .willReturn("https://cdn.moyorak.com/review1/photo1.jpg");

        final ReviewServingTime reviewServingTime =
                ReviewServingTimeFixture.fixture(1L, "5분이내", true, 5);
        final ReviewWaitingTime reviewWaitingTime =
                ReviewWaitingTimeFixture.fixture(1L, "5분이내", true, 5);

        given(reviewService.getAllReviewServingTimes()).willReturn(List.of(reviewServingTime));
        given(reviewService.getAllReviewWaitingTimes()).willReturn(List.of(reviewWaitingTime));

        // when
        final ListResponse<UserReviewResponse> result =
                userReviewFacade.getReviewWithUserByUserId(userId, request);

        // then
        assertThat(result.getData()).hasSize(1);
        final UserReviewResponse userReviewResponse = result.getData().getFirst();

        assertSoftly(
                it -> {
                    it.assertThat(userReviewResponse.id()).isEqualTo(1L);
                    it.assertThat(userReviewResponse.photoUrls())
                            .doesNotContain("s3://review1/photo1.jpg");
                    it.assertThat(userReviewResponse.servingTime()).isEqualTo("5분이내");
                    it.assertThat(userReviewResponse.waitingTime()).isEqualTo("5분이내");
                });
    }
}
