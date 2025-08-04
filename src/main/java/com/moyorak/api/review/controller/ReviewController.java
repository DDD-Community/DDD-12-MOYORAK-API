package com.moyorak.api.review.controller;

import com.moyorak.api.auth.domain.UserPrincipal;
import com.moyorak.api.review.dto.ReviewSaveRequest;
import com.moyorak.api.review.dto.ReviewServingTimeResponse;
import com.moyorak.api.review.dto.ReviewUpdateRequest;
import com.moyorak.api.review.dto.ReviewWaitingTimeResponse;
import com.moyorak.api.review.service.ReviewFacade;
import com.moyorak.api.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
@Tag(name = "[리뷰] 리뷰 API", description = "리뷰를 위한 API 입니다.")
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewFacade reviewFacade;

    @GetMapping("/reviews/serving-time")
    @Operation(summary = "리뷰 서빙 시간 조회", description = "리뷰 서빙 시간을 조회를 합니다.")
    public List<ReviewServingTimeResponse> getReviewServingTime() {
        return reviewService.getReviewServingTimeList();
    }

    @GetMapping("/reviews/waiting-time")
    @Operation(summary = "리뷰 대기 시간 조회", description = "리뷰 대기 시간을 조회를 합니다.")
    public List<ReviewWaitingTimeResponse> getReviewWaitingTime() {
        return reviewService.getReviewWaitingTimeList();
    }

    @PostMapping("/teams/{teamId}/restaurants/{teamRestaurantId}/reviews")
    @Operation(summary = "리뷰 등록", description = "리뷰를 등록 합니다.")
    public void createReview(
            @PathVariable @Positive final Long teamId,
            @PathVariable @Positive final Long teamRestaurantId,
            @RequestBody @Valid final ReviewSaveRequest reviewSaveRequest) {
        reviewFacade.createReview(teamId, teamRestaurantId, reviewSaveRequest);
    }

    @PutMapping("/teams/{teamId}/restaurants/{teamRestaurantId}/reviews/{reviewId}")
    @Operation(summary = "리뷰 수정", description = "리뷰를 수정 합니다.")
    public void updateReview(
            @PathVariable @Positive final Long teamId,
            @PathVariable @Positive final Long teamRestaurantId,
            @PathVariable @Positive final Long reviewId,
            @RequestBody @Valid final ReviewUpdateRequest reviewUpdateRequest,
            @AuthenticationPrincipal final UserPrincipal userPrincipal) {
        reviewFacade.updateReview(
                teamId, teamRestaurantId, reviewId, reviewUpdateRequest, userPrincipal.getId());
    }

    @DeleteMapping("/teams/{teamId}/restaurants/{teamRestaurantId}/reviews/{reviewId}")
    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제 합니다.")
    public void deleteReview(
            @PathVariable @Positive final Long teamId,
            @PathVariable @Positive final Long teamRestaurantId,
            @PathVariable @Positive final Long reviewId,
            @AuthenticationPrincipal final UserPrincipal userPrincipal) {
        reviewFacade.deleteReview(teamId, teamRestaurantId, reviewId, userPrincipal.getId());
    }
}
