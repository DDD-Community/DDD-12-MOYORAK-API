package com.moyorak.api.review.service;

import com.moyorak.api.image.ImageStore;
import com.moyorak.api.review.domain.FirstReviewPhotoPaths;
import com.moyorak.api.review.domain.ReviewPhoto;
import com.moyorak.api.review.dto.FirstReviewPhotoId;
import com.moyorak.api.review.dto.FirstReviewPhotoPath;
import com.moyorak.api.review.dto.PhotoPath;
import com.moyorak.api.review.dto.ReviewPhotoPath;
import com.moyorak.api.review.repository.ReviewPhotoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewPhotoService {

    private final ReviewPhotoRepository reviewPhotoRepository;
    private final ImageStore imageStore;

    @Transactional(readOnly = true)
    public FirstReviewPhotoPaths findFirstReviewPhotoPaths(final List<Long> teamRestaurantIds) {
        // 팀식당ID -> 포토 ID
        final List<FirstReviewPhotoId> firstReviewPhotoIds =
                reviewPhotoRepository.findFirstReviewPhotoIdsByTeamRestaurantIds(teamRestaurantIds);
        final List<Long> reviewPhotoIds =
                firstReviewPhotoIds.stream().map(FirstReviewPhotoId::reviewPhotoId).toList();

        // 포토 ID -> 포토 Path
        final List<FirstReviewPhotoPath> firstReviewPhotoPaths =
                reviewPhotoRepository.findFirstReviewPhotoPathsByIdIn(reviewPhotoIds);

        return FirstReviewPhotoPaths.create(teamRestaurantIds, firstReviewPhotoPaths);
    }

    @Transactional(readOnly = true)
    public List<ReviewPhotoPath> getReviewPhotoPathsGroupedByReviewId(final List<Long> reviewIds) {
        return reviewPhotoRepository.findPhotoPathsByReviewIds(reviewIds);
    }

    @Transactional(readOnly = true)
    public Page<PhotoPath> getAllReviewPhotoPathsByTeamRestaurantId(
            Long teamRestaurantId, Pageable pageable) {
        return reviewPhotoRepository
                .findPhotoPathsByTeamRestaurantId(teamRestaurantId, pageable)
                .map(p -> new PhotoPath(imageStore.getUrlFromStringPath(p.path())));
    }

    @Transactional
    public void createReviewPhoto(final List<String> photoPaths, Long reviewId) {
        photoPaths.stream()
                .map(photoPath -> ReviewPhoto.create(photoPath, reviewId))
                .forEach(reviewPhotoRepository::save);
    }
}
