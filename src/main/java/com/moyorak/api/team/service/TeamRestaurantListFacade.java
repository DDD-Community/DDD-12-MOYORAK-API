package com.moyorak.api.team.service;

import com.moyorak.api.review.domain.FirstReviewPhotoPaths;
import com.moyorak.api.review.service.ReviewPhotoService;
import com.moyorak.api.team.domain.TeamRestaurant;
import com.moyorak.api.team.domain.TeamRestaurantSummaries;
import com.moyorak.api.team.dto.ListResult;
import com.moyorak.api.team.dto.TeamRestaurantListRequest;
import com.moyorak.api.team.dto.TeamRestaurantListResponse;
import com.moyorak.global.domain.ListResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamRestaurantListFacade {

    private final TeamRestaurantService teamRestaurantService;
    private final ReviewPhotoService reviewPhotoService;

    @Transactional(readOnly = true)
    public ListResponse<TeamRestaurantListResponse> getRestaurants(
            final Long teamId, final TeamRestaurantListRequest teamRestaurantListRequest) {

        final Page<TeamRestaurant> teamRestaurantPage =
                teamRestaurantService.getTeamRestaurants(teamId, teamRestaurantListRequest);

        List<Long> teamRestaurantIds =
                teamRestaurantPage.getContent().stream().map(TeamRestaurant::getId).toList();
        // 팀 식당 id로 필요한 정보 가져오기
        final TeamRestaurantSummaries teamRestaurantSummaries =
                teamRestaurantService.findByIdsAndUse(teamRestaurantIds, true);

        // 팀 식당별로 리뷰 첫 사진 정보 가져오기
        final FirstReviewPhotoPaths firstReviewPhotoPaths =
                reviewPhotoService.findFirstReviewPhotoPaths(teamRestaurantIds);

        // 팀 식당 정보로 응답 조합
        final List<TeamRestaurantListResponse> teamRestaurantListResponses =
                teamRestaurantSummaries.toListResponse(firstReviewPhotoPaths);

        final ListResult listResult =
                new ListResult(
                        teamRestaurantIds,
                        teamRestaurantListRequest.toPageable(),
                        teamRestaurantPage.getTotalElements());

        return ListResponse.from(listResult.toPage(teamRestaurantListResponses));
    }
}
