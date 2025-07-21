package com.moyorak.api.team.service;

import com.moyorak.api.review.domain.FirstReviewPhotoPaths;
import com.moyorak.api.review.service.ReviewPhotoService;
import com.moyorak.api.team.domain.TeamRestaurantSummaries;
import com.moyorak.api.team.dto.SearchResult;
import com.moyorak.api.team.dto.TeamRestaurantSearchRequest;
import com.moyorak.api.team.dto.TeamRestaurantSearchResponse;
import com.moyorak.global.domain.ListResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamRestaurantSearchFacade {

    private final TeamRestaurantService teamRestaurantService;
    private final TeamRestaurantSearchService teamRestaurantSearchService;
    private final ReviewPhotoService reviewPhotoService;
    private final TeamRestaurantEventPublisher teamRestaurantEventPublisher;

    @Transactional(readOnly = true)
    public ListResponse<TeamRestaurantSearchResponse> search(
            final Long userId,
            final Long teamId,
            final TeamRestaurantSearchRequest teamRestaurantSearchRequest) {

        // 서치 서비스에서 id들 가져오기
        final SearchResult searchResult =
                teamRestaurantSearchService.search(
                        teamId,
                        teamRestaurantSearchRequest.getKeyword(),
                        teamRestaurantSearchRequest.toPageable());

        // 팀 식당 id로 필요한 정보 가져오기
        final TeamRestaurantSummaries teamRestaurantSummaries =
                teamRestaurantService.findByIdsAndUse(searchResult.ids(), true);

        // 팀 식당별로 리뷰 첫 사진 정보 가져오기
        final FirstReviewPhotoPaths firstReviewPhotoPaths =
                reviewPhotoService.findFirstReviewPhotoPaths(
                        teamRestaurantSummaries.getTeamRestaurantIds());

        // 팀 식당 정보로 응답 조합
        final List<TeamRestaurantSearchResponse> teamRestaurantSearchResponses =
                teamRestaurantSummaries.toResponse(firstReviewPhotoPaths);

        // 검색 기록 이벤트 발행
        teamRestaurantEventPublisher.publishSearchEvent(
                userId, teamId, teamRestaurantSearchRequest.getKeyword());

        return ListResponse.from(searchResult.toPage(teamRestaurantSearchResponses));
    }
}
