package com.moyorak.api.party.dto;

import com.moyorak.api.party.domain.VoteRestaurantCandidate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(title = "[파티 식당] 파티 식당 추가 요청 DTO")
public record PartyRestaurantAppendRequest(
        @NotNull @Positive @Schema(description = "팀 식당 ID", example = "1") Long teamRestaurantId,
        @NotNull @Positive @Schema(description = "투표 ID", example = "1") Long voteId) {
    public VoteRestaurantCandidate toVoteRestaurantCandidate() {
        return VoteRestaurantCandidate.create(teamRestaurantId, voteId);
    }
}
