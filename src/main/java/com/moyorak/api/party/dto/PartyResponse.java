package com.moyorak.api.party.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(title = "[파티] 파티 상세 조회 DTO")
public record PartyResponse(
        @Schema(description = "파티 고유 ID", example = "1") Long id,
        @Schema(description = "파티 제목", example = "분식 가실 분") String title,
        @Schema(description = "파티 내용", example = "3개 음식점 중에 골라보시죠") String content,
        @Schema(description = "투표 정보") VoteResponse vote,
        @ArraySchema(
                        schema = @Schema(implementation = RestaurantCandidateResponse.class),
                        arraySchema = @Schema(description = "후보 식당 목록"))
                List<RestaurantCandidateResponse> candidates,
        @ArraySchema(
                        schema = @Schema(implementation = VoterResponse.class),
                        arraySchema = @Schema(description = "투표자 목록"))
                List<VoterResponse> voters,
        @Schema(description = "참석 여부", example = "true") boolean attended) {
    public static PartyResponse from(
            final PartyInfo partyInfo,
            final VoteInfo voteInfo,
            final List<RestaurantCandidateResponse> candidates,
            final List<Voter> voters,
            final boolean attended) {

        final List<VoterResponse> voterResponses =
                voters.stream().map(VoterResponse::from).toList();

        return new PartyResponse(
                partyInfo.id(),
                partyInfo.title(),
                partyInfo.content(),
                VoteResponse.from(voteInfo),
                candidates,
                voterResponses,
                attended);
    }
}
