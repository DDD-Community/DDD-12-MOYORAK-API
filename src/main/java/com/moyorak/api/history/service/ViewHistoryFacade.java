package com.moyorak.api.history.service;

import com.moyorak.api.history.dto.ViewHistoryListResponse;
import com.moyorak.api.history.dto.ViewHistoryRequest;
import com.moyorak.api.history.dto.ViewHistorySummaries;
import com.moyorak.api.review.domain.FirstReviewPhotoPaths;
import com.moyorak.api.review.service.ReviewPhotoService;
import com.moyorak.api.team.domain.TeamRestaurantSummaries;
import com.moyorak.api.team.service.TeamRestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ViewHistoryFacade {

    private final ViewHistoryService viewHistoryService;
    private final TeamRestaurantService teamRestaurantService;
    private final ReviewPhotoService reviewPhotoService;

    @Transactional(readOnly = true)
    public ViewHistoryListResponse getViewHistories(final ViewHistoryRequest request) {
        final ViewHistorySummaries viewHistorySummaries =
                viewHistoryService.getViewHistorySummaries(request);

        // 팀 식당 id로 필요한 정보 가져오기
        final TeamRestaurantSummaries teamRestaurantSummaries =
                teamRestaurantService.findByIdsAndUse(
                        viewHistorySummaries.getTeamRestaurantIds(), true);

        // 팀 식당별로 리뷰 첫 사진 정보 가져오기
        final FirstReviewPhotoPaths firstReviewPhotoPaths =
                reviewPhotoService.findFirstReviewPhotoPaths(
                        teamRestaurantSummaries.getTeamRestaurantIds());

        return ViewHistoryListResponse.from(
                viewHistorySummaries, teamRestaurantSummaries, firstReviewPhotoPaths);
    }
}
