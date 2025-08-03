package com.moyorak.api.review.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.moyorak.api.image.ImageStore;
import com.moyorak.api.review.domain.FirstReviewPhotoPaths;
import com.moyorak.api.review.dto.FirstReviewPhotoId;
import com.moyorak.api.review.dto.FirstReviewPhotoPath;
import com.moyorak.api.review.dto.ReviewPhotoPath;
import com.moyorak.api.review.repository.ReviewPhotoRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReviewPhotoServiceTest {

    @InjectMocks private ReviewPhotoService reviewPhotoService;

    @Mock private ReviewPhotoRepository reviewPhotoRepository;

    @Mock private ImageStore imageStore;

    @Nested
    @DisplayName("첫번째 리뷰 이미지 조회시")
    class FindFirstReviewPhoto {

        private final Long teamRestaurantId = 1L;
        private final Long photoId = 100L;
        private final String photoPath = "dev/somepath/photo.jpg";
        private final String photoUrl = "http://photo.jpg";

        @Test
        @DisplayName("이미지가 없는 경우 photoPath가 null인 결과를 반환한다")
        void noPhoto() {
            // given
            final List<Long> teamRestaurantIds = List.of(teamRestaurantId);

            given(
                            reviewPhotoRepository.findFirstReviewPhotoIdsByTeamRestaurantIds(
                                    teamRestaurantIds))
                    .willReturn(List.of());

            given(reviewPhotoRepository.findFirstReviewPhotoPathsByIdIn(List.of()))
                    .willReturn(List.of());

            // when
            final FirstReviewPhotoPaths result =
                    reviewPhotoService.findFirstReviewPhotoPaths(teamRestaurantIds);

            // then
            final String reviewPhotoPath = result.getPhotoPath(teamRestaurantId);
            assertThat(reviewPhotoPath).isNull();
        }

        @Test
        @DisplayName("이미지가 존재하는 경우 photoURL를 포함한 결과를 반환한다")
        void hasPhoto() {
            // given
            final List<Long> teamRestaurantIds = List.of(teamRestaurantId);

            given(
                            reviewPhotoRepository.findFirstReviewPhotoIdsByTeamRestaurantIds(
                                    teamRestaurantIds))
                    .willReturn(List.of(new FirstReviewPhotoId(teamRestaurantId, photoId)));

            given(reviewPhotoRepository.findFirstReviewPhotoPathsByIdIn(List.of(photoId)))
                    .willReturn(List.of(new FirstReviewPhotoPath(teamRestaurantId, photoPath)));

            given(imageStore.getUrlFromStringPath(photoPath)).willReturn(photoUrl);

            // when
            final FirstReviewPhotoPaths result =
                    reviewPhotoService.findFirstReviewPhotoPaths(teamRestaurantIds);

            // then\
            final String reviewPhotoPath = result.getPhotoPath(teamRestaurantId);
            assertThat(reviewPhotoPath).isEqualTo(photoUrl);
        }
    }

    @Nested
    @DisplayName("리뷰 별 사진 조회 시")
    class GetReviewPhotoPaths {
        private final Long reviewId1 = 1L;
        private final Long reviewId2 = 2L;

        @Test
        @DisplayName("해당 리뷰 ID에 사진이 존재하면 경로 리스트를 반환한다")
        void shouldReturnPhotoPathsWhenExists() {
            // given
            List<Long> reviewIds = List.of(reviewId1, reviewId2);

            List<ReviewPhotoPath> reviewPhotoPathList =
                    List.of(
                            new ReviewPhotoPath(reviewId1, "s3://review1/photo1.jpg"),
                            new ReviewPhotoPath(reviewId1, "s3://review1/photo2.jpg"),
                            new ReviewPhotoPath(reviewId2, "s3://review2/photo3.jpg"));

            given(reviewPhotoRepository.findPhotoPathsByReviewIds(reviewIds))
                    .willReturn(reviewPhotoPathList);

            // when
            List<ReviewPhotoPath> reviewPhotoPaths =
                    reviewPhotoService.getReviewPhotoPathsGroupedByReviewId(reviewIds);

            // then
            assertThat(reviewPhotoPaths.get(0).reviewId()).isEqualTo(reviewId1);
            assertThat(reviewPhotoPaths.get(0).reviewPhotoPath())
                    .isEqualTo("s3://review1/photo1.jpg");
        }

        @Test
        @DisplayName("해당 리뷰 ID에 사진이 없으면 빈 리스트를 반환한다")
        void shouldReturnEmptyListWhenNoPhotosExist() {
            // given
            List<Long> reviewIds = List.of(reviewId1);
            given(reviewPhotoRepository.findPhotoPathsByReviewIds(reviewIds)).willReturn(List.of());

            // when
            List<ReviewPhotoPath> reviewPhotoPaths =
                    reviewPhotoService.getReviewPhotoPathsGroupedByReviewId(reviewIds);

            // then
            assertThat(reviewPhotoPaths.size()).isEqualTo(0);
        }
    }
}
