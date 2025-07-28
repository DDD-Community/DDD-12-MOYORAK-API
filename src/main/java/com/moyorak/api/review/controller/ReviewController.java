package com.moyorak.api.review.controller;

import com.moyorak.api.review.dto.ReviewServingTimeResponse;
import com.moyorak.api.review.dto.ReviewWaitingTimeResponse;
import com.moyorak.api.review.service.ReviewFacade;
import com.moyorak.api.review.service.ReviewService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
    public List<ReviewServingTimeResponse> getReviewServingTime() {
        return reviewService.getReviewServingTimeList();
    }

    @GetMapping("/reviews/waiting-time")
    public List<ReviewWaitingTimeResponse> getReviewWaitingTime() {
        return reviewService.getReviewWaitingTimeList();
    }
}
