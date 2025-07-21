package com.moyorak.api.team.domain;

import com.moyorak.api.review.domain.FirstReviewPhotoPaths;
import com.moyorak.api.team.dto.TeamRestaurantListResponse;
import com.moyorak.api.team.dto.TeamRestaurantSearchResponse;
import com.moyorak.api.team.dto.TeamRestaurantSummary;
import java.util.List;

public class TeamRestaurantSummaries {

    private final List<TeamRestaurantSummary> summaries;

    private TeamRestaurantSummaries(final List<TeamRestaurantSummary> summary) {
        this.summaries = summary;
    }

    public static TeamRestaurantSummaries create(final List<TeamRestaurantSummary> summary) {
        return new TeamRestaurantSummaries(summary);
    }

    public List<Long> getTeamRestaurantIds() {
        return summaries.stream().map(TeamRestaurantSummary::teamRestaurantId).toList();
    }

    public List<TeamRestaurantSearchResponse> toResponse(FirstReviewPhotoPaths photoPaths) {
        return summaries.stream()
                .map(
                        teamRestaurantSearchSummary ->
                                TeamRestaurantSearchResponse.from(
                                        teamRestaurantSearchSummary,
                                        photoPaths.getPhotoPath(
                                                teamRestaurantSearchSummary.teamRestaurantId())))
                .toList();
    }

    public List<TeamRestaurantListResponse> toListResponse(FirstReviewPhotoPaths photoPaths) {
        return summaries.stream()
                .map(
                        teamRestaurantSearchSummary ->
                                TeamRestaurantListResponse.from(
                                        teamRestaurantSearchSummary,
                                        photoPaths.getPhotoPath(
                                                teamRestaurantSearchSummary.teamRestaurantId())))
                .toList();
    }
}
