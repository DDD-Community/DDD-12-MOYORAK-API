package com.moyorak.api.review.service;

import com.moyorak.api.review.domain.Review;
import com.moyorak.api.review.domain.ReviewServingTime;
import com.moyorak.api.review.domain.ReviewWaitingTime;
import com.moyorak.api.review.dto.ReviewSaveRequest;
import com.moyorak.api.review.dto.ReviewServingTimeResponse;
import com.moyorak.api.review.dto.ReviewWaitingTimeResponse;
import com.moyorak.api.review.dto.ReviewWithUserProjection;
import com.moyorak.api.review.repository.ReviewRepository;
import com.moyorak.api.review.repository.ReviewServingTimeRepository;
import com.moyorak.api.review.repository.ReviewWaitingTimeRepository;
import com.moyorak.config.exception.BusinessException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewServingTimeRepository reviewServingTimeRepository;
    private final ReviewWaitingTimeRepository reviewWaitingTimeRepository;

    @Transactional(readOnly = true)
    public Page<ReviewWithUserProjection> getReviewWithUserByTeamRestaurantId(
            Long teamRestaurantId, Pageable pageable) {
        return reviewRepository.findReviewWithUserByTeamRestaurantId(teamRestaurantId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ReviewWithUserProjection> getReviewWithUserByUserId(
            Long userId, Pageable pageable) {
        return reviewRepository.findReviewWithUserByUserId(userId, pageable);
    }

    @Transactional(readOnly = true)
    public List<ReviewServingTimeResponse> getReviewServingTimeList() {
        return ReviewServingTimeResponse.fromList(getAllReviewServingTimes());
    }

    @Transactional(readOnly = true)
    public List<ReviewServingTime> getAllReviewServingTimes() {
        List<ReviewServingTime> reviewServingTimes = reviewServingTimeRepository.findAllByUse(true);
        if (reviewServingTimes.isEmpty()) {
            throw new BusinessException("서빙 시간 데이터가 없습니다.");
        }
        return reviewServingTimes;
    }

    @Transactional(readOnly = true)
    public ReviewServingTime getReviewServingTime(final Long id) {
        return reviewServingTimeRepository
                .findByIdAndUseIsTrue(id)
                .orElseThrow(() -> new BusinessException("서빙 시간 데이터가 없습니다."));
    }

    @Transactional(readOnly = true)
    public List<ReviewWaitingTimeResponse> getReviewWaitingTimeList() {
        return ReviewWaitingTimeResponse.fromList(getAllReviewWaitingTimes());
    }

    @Transactional(readOnly = true)
    public List<ReviewWaitingTime> getAllReviewWaitingTimes() {
        List<ReviewWaitingTime> reviewWaitingTimes = reviewWaitingTimeRepository.findAllByUse(true);
        if (reviewWaitingTimes.isEmpty()) {
            throw new BusinessException("대기 시간 데이터가 없습니다.");
        }
        return reviewWaitingTimes;
    }

    @Transactional(readOnly = true)
    public ReviewWaitingTime getReviewWaitingTime(final Long id) {
        return reviewWaitingTimeRepository
                .findByIdAndUseIsTrue(id)
                .orElseThrow(() -> new BusinessException("대기 시간 데이터가 없습니다."));
    }

    @Transactional
    public Review crateReview(
            final ReviewSaveRequest reviewSaveRequest,
            final Integer servingTimeValue,
            final Integer waitingTimeValue,
            Long teamRestaurantId) {
        return reviewRepository.save(
                reviewSaveRequest.toReview(servingTimeValue, waitingTimeValue, teamRestaurantId));
    }

    @Transactional
    public Review getReviewForUpdate(final Long id) {
        return reviewRepository
                .findByIdAndUseIsTrue(id)
                .orElseThrow(() -> new BusinessException("리뷰가 존재하지 않습니다."));
    }
}
