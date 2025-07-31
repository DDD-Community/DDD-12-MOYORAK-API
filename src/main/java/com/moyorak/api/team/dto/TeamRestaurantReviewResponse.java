package com.moyorak.api.team.dto;

import com.moyorak.api.review.domain.ReviewPhotoPaths;
import com.moyorak.api.review.domain.ReviewTimeLabels;
import com.moyorak.api.review.dto.ReviewWithUserProjection;
import java.util.List;
import org.springframework.data.domain.Page;

public record TeamRestaurantReviewResponse(
        Long id,
        String extraText,
        Integer score,
        String servingTime,
        String waitingTime,
        String userNickname,
        String userProfileImageUrl,
        List<String> photoUrls,
        String createdDate) {
    public static Page<TeamRestaurantReviewResponse> from(
            Page<ReviewWithUserProjection> review,
            ReviewPhotoPaths reviewPhotoPaths,
            ReviewTimeLabels reviewTimeLabels) {
        return review.map(
                r ->
                        new TeamRestaurantReviewResponse(
                                r.id(),
                                r.extraText(),
                                r.score(),
                                reviewTimeLabels.getServingLabel(r.id()),
                                reviewTimeLabels.getWaitingLabel(r.id()),
                                r.name(),
                                r.profileImage(),
                                reviewPhotoPaths.getPhotoPaths(r.id()),
                                reviewTimeLabels.convertDate(r.createdDate().toLocalDate())));
    }
}
